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

        // TODO: Check if the buddy is allowed to teach the mission.

        // Update student
        if (student.getMissionCompletions().containsKey(missionId)) {
            MissionCompletion completion = student.getMissionCompletions().get(missionId);
            completion.setStudyCount(completion.getStudyCount() + 1);
        } else {
            long completionId = factory().allocateId(MissionCompletion.class).getId();
            MissionCompletion completion =
                    new MissionCompletion(completionId, missionRef, 1, 0);
            student.getMissionCompletions().put(missionId, completion);
        }
        chargePoints(student, mission, buddyRef, studentRef);
        ofy().save().entity(student).now();

        // Update buddy.
        if (buddy.getMissionCompletions().containsKey(missionId)) {
            MissionCompletion completion = buddy.getMissionCompletions().get(missionId);
            completion.setMentorCount(completion.getMentorCount() + 1);
        } else {
            if (buddy.isAdmin()) {
                long completionId = factory().allocateId(MissionCompletion.class).getId();
                MissionCompletion completion =
                        new MissionCompletion(completionId, missionRef, 0, 1);
                buddy.getMissionCompletions().put(missionId, completion);
            } else {
                // This is an error case because a mission has to be studied at least once before
                // being allowed to teach.
                // TODO: throw exception
            }
        }
        buddy.addPoints(UserPoints.PointType.teach, 1);
        ofy().save().entity(buddy).now();

        // Save point transfer log for buddy
        PointsTransferAuditLog auditLog = new PointsTransferAuditLog(
                PointsTransferAuditLog.PointsTransferType.teachMission,
                buddyRef,
                studentRef,
                UserPoints.PointType.teach,
                1);
        ofy().save().entity(auditLog);

        // Save audit log
        MentoringAuditLog audit = new MentoringAuditLog(
                Ref.create(Key.create(OxygenUser.class, studentId)),
                Ref.create(Key.create(OxygenUser.class, buddy.getId())),
                null,
                missionRef,
                true,
                System.currentTimeMillis());
        ofy().save().entity(audit);

        // Send a Firebase message to the student to confirm completion.
        FirebaseUtil.sendMissionCompletionToStudent(
                student.getFirebaseToken(),
                new UserBean(buddy),
                missionId);
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
