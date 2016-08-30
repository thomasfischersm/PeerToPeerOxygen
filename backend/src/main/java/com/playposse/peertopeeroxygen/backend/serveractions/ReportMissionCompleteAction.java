package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;

import java.io.IOException;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that a buddy calls to notify the server that the mission is complete. Also, the
 * student's Android device is notified with Firebase message.
 */
public class ReportMissionCompleteAction extends ServerAction {

    public static void reportMissionComplete(
            Long sessionId,
            Long studentId,
            Long missionId)
            throws UnauthorizedException, IOException {

        // Load relevant data.
        OxygenUser buddy = loadUserBySessionId(sessionId);
        OxygenUser student = loadUserById(studentId);
        Ref<Mission> missionRef = Ref.create(Key.create(Mission.class, missionId));
        Ref<OxygenUser> buddyRef = Ref.create(Key.create(OxygenUser.class, buddy.getId()));
        Ref<OxygenUser> studentRef = Ref.create(Key.create(OxygenUser.class, student.getId()));
        Mission mission = ofy().load().type(Mission.class).id(missionId).now();

        // Check if the buddy is allowed to teach the mission.
        // Update buddy first because this includes checks if the buddy is allowed to teach.
        updateBuddy(missionId, buddy, missionRef);
        updateStudent(missionId, student, missionRef, buddyRef, studentRef, mission);
        updatePointTransferLogForBuddy(buddyRef, studentRef);
        updateMentoringLog(studentId, buddy, missionRef);


        // Send a Firebase message to the student to confirm completion.
        FirebaseUtil.sendMissionCompletionToStudent(
                student.getFirebaseToken(),
                new UserBean(buddy),
                missionId);
    }

    private static void updateStudent(
            Long missionId,
            OxygenUser student,
            Ref<Mission> missionRef,
            Ref<OxygenUser> buddyRef,
            Ref<OxygenUser> studentRef,
            Mission mission) {

        final MissionCompletion completion;
        if (student.getMissionCompletions().containsKey(missionId)) {
            completion = student.getMissionCompletions().get(missionId);
            completion.setStudyCount(completion.getStudyCount() + 1);
        } else {
            long completionId = factory().allocateId(MissionCompletion.class).getId();
            completion =
                    new MissionCompletion(completionId, missionRef, 1, 0, false, false);
            student.getMissionCompletions().put(missionId, completion);
        }
        chargePoints(student, mission, buddyRef, studentRef);
        if (completion.getStudyCount() >= mission.getMinimumStudyCount()) {
            // Be sure to avoid accidentally setting studyComplete to false if the mission study
            // minimum was raised after the student completed the old minimum.
            completion.setStudyComplete(true);
        }
        ofy().save().entity(student).now();
    }

    private static void updateBuddy(Long missionId, OxygenUser buddy, Ref<Mission> missionRef)
            throws UnauthorizedException {

        if (buddy.getMissionCompletions().containsKey(missionId)) {
            MissionCompletion completion = buddy.getMissionCompletions().get(missionId);
            if (completion.isMentorCheckoutComplete() || buddy.isAdmin()) {
                completion.setMentorCount(completion.getMentorCount() + 1);
            } else {
                throw new UnauthorizedException("The buddy " + buddy.getId()
                        + " isn't allowed to teach mission " + missionId);
            }
        } else {
            if (buddy.isAdmin()) {
                long completionId = factory().allocateId(MissionCompletion.class).getId();
                MissionCompletion completion =
                        new MissionCompletion(completionId, missionRef, 0, 1, false, false);
                buddy.getMissionCompletions().put(missionId, completion);
            } else {
                throw new UnauthorizedException("The buddy " + buddy.getId()
                        + " isn't allowed to teach mission " + missionId
                        + ". The completion entry is missing.");
            }
        }
        buddy.addPoints(UserPoints.PointType.teach, 1);
        ofy().save().entity(buddy).now();
    }

    private static void updatePointTransferLogForBuddy(
            Ref<OxygenUser> buddyRef,
            Ref<OxygenUser> studentRef) {

        PointsTransferAuditLog auditLog = new PointsTransferAuditLog(
                PointsTransferAuditLog.PointsTransferType.teachMission,
                buddyRef,
                studentRef,
                UserPoints.PointType.teach,
                1);
        ofy().save().entity(auditLog);
    }

    private static void updateMentoringLog(Long studentId, OxygenUser buddy, Ref<Mission> missionRef) {
        // Save audit log
        MentoringAuditLog audit = new MentoringAuditLog(
                Ref.create(Key.create(OxygenUser.class, studentId)),
                Ref.create(Key.create(OxygenUser.class, buddy.getId())),
                null,
                missionRef,
                true,
                System.currentTimeMillis());
        ofy().save().entity(audit);
    }

    private static void chargePoints(
            OxygenUser student,
            Mission mission,
            Ref<OxygenUser> buddyRef,
            Ref<OxygenUser> studentRef) {

        for (Map.Entry<UserPoints.PointType, Integer> entry : mission.getPointCostMap().entrySet()) {
            Integer pointCount = 0 - entry.getValue();
            if (pointCount != 0) {
                UserPoints.PointType pointType = entry.getKey();
                student.addPoints(pointType, pointCount);

                PointsTransferAuditLog auditLog = new PointsTransferAuditLog(
                        PointsTransferAuditLog.PointsTransferType.teachMission,
                        studentRef,
                        buddyRef,
                        pointType,
                        pointCount);
                ofy().save().entity(auditLog);
            }
        }
    }
}
