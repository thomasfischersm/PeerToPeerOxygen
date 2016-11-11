package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionCompletionToStudentServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendPracticaUserUpdateServerAction;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that a buddy calls to notify the server that the mission is complete. Also, the
 * student's Android device is notified with Firebase message.
 */
public class ReportMissionCompleteServerAction extends ServerAction {

    private static final Logger log =
            Logger.getLogger(ReportMissionCompleteServerAction.class.getName());

    public static void reportMissionComplete(
            Long sessionId,
            Long studentId,
            Long missionId,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Load buddy.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser buddy = findOxygenUserByDomain(masterUser, domainId);
        verifyUserByDomain(buddy, domainId);

        // Load student.
        OxygenUser student = loadOxygenUserById(studentId, domainId);

        // Create references.
        Ref<Mission> missionRef = Ref.create(Key.create(Mission.class, missionId));
        Ref<OxygenUser> buddyRef = Ref.create(Key.create(OxygenUser.class, buddy.getId()));
        Ref<OxygenUser> studentRef = Ref.create(Key.create(OxygenUser.class, student.getId()));
        Ref<Domain> domainRef = RefUtil.createDomainRef(domainId);

        // Load mission.
        Mission mission = ofy().load().type(Mission.class).id(missionId).now();

        List<MissionTree> missionTreeQueryResult = ofy()
                .load()
                .type(MissionTree.class)
                .filter("bossMissionRef", Key.create(Mission.class, mission.getId()))
                .list();
        MissionTree missionTree = (missionTreeQueryResult.size() > 0) ? missionTreeQueryResult.get(0) : null;
        log.info("Found mission tree: " + missionTree);

        // Check if the buddy is allowed to teach the mission.
        // Update buddy first because this includes checks if the buddy is allowed to teach.
        updateBuddy(missionId, buddy, missionRef);
        updateStudent(missionId, student, missionRef, buddyRef, studentRef, domainRef, mission, missionTree);
        updatePointTransferLogForBuddy(buddyRef, studentRef, domainRef);
        updateMentoringLog(studentId, buddy, missionRef, domainRef);

        updateMissionStats(missionId, domainId);

        // Send a Firebase message to the student to confirm completion.
        SendMissionCompletionToStudentServerAction.sendMissionCompletionToStudent(
                student.getMasterUserRef().get().getFirebaseToken(),
                buddy,
                missionId);
    }

    private static void updateStudent(
            Long missionId,
            OxygenUser student,
            Ref<Mission> missionRef,
            Ref<OxygenUser> buddyRef,
            Ref<OxygenUser> studentRef,
            Ref<Domain> domainRef,
            Mission mission,
            @Nullable MissionTree missionTree) throws IOException {

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

        chargePoints(student, mission, buddyRef, studentRef, domainRef);

        if (completion.getStudyCount() >= mission.getMinimumStudyCount()) {
            // Be sure to avoid accidentally setting studyComplete to false if the mission study
            // minimum was raised after the student completed the old minimum.
            completion.setStudyComplete(true);

            if ((missionTree != null)
                    && (missionTree.getBossMissionRef() != null)
                    && (mission.getId().equals(missionTree.getBossMissionRef().getKey().getId()))) {
                LevelCompletion levelCompletion = getLevelCompletion(student, missionTree.getId());

                // A user can complete the boss mission multiple times.
                if (levelCompletion == null) {
                    Ref<MissionTree> missionTreeRef =
                            Ref.create(Key.create(MissionTree.class, missionTree.getId()));
                    LevelCompletion newLevelCompletion =
                            new LevelCompletion(System.currentTimeMillis(), missionTreeRef);
                    student.getLevelCompletions().add(newLevelCompletion);
                }
            }

            if (student.getActivePracticaRef() != null) {
                long practicaId = student.getActivePracticaRef().getKey().getId();
                SendPracticaUserUpdateServerAction.sendPracticaUserUpdate(student, practicaId);
            }
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
            Ref<OxygenUser> studentRef,
            Ref<Domain> domainRef) {

        PointsTransferAuditLog auditLog = new PointsTransferAuditLog(
                PointsTransferAuditLog.PointsTransferType.teachMission,
                buddyRef,
                studentRef,
                UserPoints.PointType.teach,
                1,
                domainRef);
        ofy().save().entity(auditLog);
    }

    private static void updateMentoringLog(
            Long studentId,
            OxygenUser buddy,
            Ref<Mission> missionRef,
            Ref<Domain> domainRef) {

        // Save audit log
        MentoringAuditLog audit = new MentoringAuditLog(
                Ref.create(Key.create(OxygenUser.class, studentId)),
                Ref.create(Key.create(OxygenUser.class, buddy.getId())),
                null,
                missionRef,
                true,
                System.currentTimeMillis(),
                domainRef);
        ofy().save().entity(audit);
    }

    private static void chargePoints(
            OxygenUser student,
            Mission mission,
            Ref<OxygenUser> buddyRef,
            Ref<OxygenUser> studentRef,
            Ref<Domain> domainRef) {

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
                        pointCount,
                        domainRef);
                ofy().save().entity(auditLog);
            }
        }
    }

    private static void updateMissionStats(Long missionId, long domainId)
            throws BadRequestException {

        final MissionStats missionStats = getMissionStats(missionId, domainId);

        missionStats.incrementCompletion();

        ofy().save().entity(missionStats);
    }
}
