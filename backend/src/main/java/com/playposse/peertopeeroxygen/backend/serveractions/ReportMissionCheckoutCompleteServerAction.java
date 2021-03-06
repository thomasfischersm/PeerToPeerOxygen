package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionCompletionToBuddyServerAction;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that a senior buddy calls to notify the server that the mission checkout is
 * complete. Also, the buddy's Android device is notified with Firebase message. The notified
 * buddy can then complete the mission itself.
 */
public class ReportMissionCheckoutCompleteServerAction extends ServerAction {

    public static void reportMissionCheckoutComplete(
            Long sessionId,
            Long studentId,
            Long buddyId,
            Long missionId,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Load student.
        OxygenUser student = loadOxygenUserById(studentId, domainId);
        verifyUserByDomain(student, domainId);

        // Load buddy.
        OxygenUser buddy = loadOxygenUserById(buddyId, domainId);
        verifyUserByDomain(buddy, domainId);

        // Load senior buddy.
        MasterUser masterSeniorBuddy = loadMasterUserBySessionId(sessionId);
        OxygenUser seniorBuddy = findOxygenUserByDomain(masterSeniorBuddy, domainId);

        Ref<Mission> missionRef = Ref.create(Key.create(Mission.class, missionId));
        Ref<OxygenUser> studentRef = Ref.create(Key.create(OxygenUser.class, student.getId()));
        Ref<OxygenUser> buddyRef = Ref.create(Key.create(OxygenUser.class, buddy.getId()));
        Ref<OxygenUser> seniorBuddyRef = Ref.create(Key.create(OxygenUser.class, seniorBuddy.getId()));
        Ref<Domain> domainRef = RefUtil.createDomainRef(domainId);

        updateSeniorBuddy(seniorBuddy, missionRef);
        updateBuddy(buddy, missionRef);
        updateMentoringLog(studentRef, buddyRef, seniorBuddyRef, missionRef, domainRef);
        updatePointTransferLogForSeniorBuddy(seniorBuddyRef, buddyRef, domainRef);


        // Send a Firebase message to the student to confirm completion.
        SendMissionCompletionToBuddyServerAction.sendMissionCompletionToBuddy(
                buddy.getMasterUserRef().get().getFirebaseToken(),
                student,
                missionId);
    }

    private static void updateBuddy(OxygenUser buddy, Ref<Mission> missionRef)
            throws UnauthorizedException {

        Long missionId = missionRef.getKey().getId();

        final MissionCompletion completion;
        if (buddy.getMissionCompletions().containsKey(missionId)) {
            completion = buddy.getMissionCompletions().get(missionId);
            completion.setMentorCheckoutComplete(true);
        } else {
            if (buddy.isAdmin()) {
                long completionId = factory().allocateId(MissionCompletion.class).getId();
                completion =
                        new MissionCompletion(completionId, missionRef, 0, 1, false, true);
                buddy.getMissionCompletions().put(missionId, completion);
            } else {
                // Impossible case. A buddy cannot be ready to teach without studying, which means
                // that there should be a completion entry.
                throw new UnauthorizedException("The buddy " + buddy.getId()
                        + " has no completion entry for the mission:" + missionId);
            }
        }
        ofy().save().entity(buddy).now();
    }

    private static void updateSeniorBuddy(OxygenUser seniorBuddy, Ref<Mission> missionRef)
            throws UnauthorizedException {

        Long missionId = missionRef.getKey().getId();
        if (seniorBuddy.getMissionCompletions().containsKey(missionId)) {
            MissionCompletion completion = seniorBuddy.getMissionCompletions().get(missionId);
            if (completion.isMentorCheckoutComplete() || seniorBuddy.isAdmin()) {
                completion.setMentorCount(completion.getMentorCount() + 1);
            } else {
                throw new UnauthorizedException("The senior buddy " + seniorBuddy.getId()
                        + " isn't allowed to teach mission " + missionId);
            }
        } else {
            if (seniorBuddy.isAdmin()) {
                long completionId = factory().allocateId(MissionCompletion.class).getId();
                MissionCompletion completion =
                        new MissionCompletion(completionId, missionRef, 0, 1, false, false);
                seniorBuddy.getMissionCompletions().put(missionId, completion);
            } else {
                throw new UnauthorizedException("The buddy " + seniorBuddy.getId()
                        + " isn't allowed to teach mission " + missionId
                        + ". The completion entry is missing.");
            }
        }
        seniorBuddy.addPoints(UserPoints.PointType.teach, 1);
        ofy().save().entity(seniorBuddy).now();
    }

    private static void updatePointTransferLogForSeniorBuddy(
            Ref<OxygenUser> seniorBuddyRef,
            Ref<OxygenUser> buddyRef,
            Ref<Domain> domainRef) {

        PointsTransferAuditLog auditLog = new PointsTransferAuditLog(
                PointsTransferAuditLog.PointsTransferType.teachMission,
                seniorBuddyRef,
                buddyRef,
                UserPoints.PointType.teach,
                1,
                domainRef);
        ofy().save().entity(auditLog);
    }

    private static void updateMentoringLog(
            Ref<OxygenUser> studentRef,
            Ref<OxygenUser> buddyRef,
            Ref<OxygenUser> seniorBuddyRef,
            Ref<Mission> missionRef,
            Ref<Domain> domainRef) {

        // Save audit log
        MentoringAuditLog audit = new MentoringAuditLog(
                studentRef,
                buddyRef,
                seniorBuddyRef,
                missionRef,
                true,
                System.currentTimeMillis(),
                domainRef);
        ofy().save().entity(audit);
    }
}
