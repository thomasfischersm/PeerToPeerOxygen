package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.exceptions.BuddyLacksMissionExperienceException;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionInviteToSeniorBuddyServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;
import java.util.Map;

/**
 * A server action that invites a senior buddy to supervise a buddy who is teaching a mission for
 * the first time.
 */
public class InviteSeniorBuddyToMissionServerAction extends ServerAction {

    public static UserBean inviteSeniorBuddyToMission(
            Long sessionId,
            Long studentId,
            Long seniorBuddyId,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId,
            Long domainId)
            throws
            UnauthorizedException,
            IOException,
            BuddyLacksMissionExperienceException,
            BadRequestException {

        // Load student.
        OxygenUser student = loadOxygenUserById(studentId, domainId);
        verifyUserByDomain(student, domainId);

        // Load buddy.
        MasterUser masterBuddy = loadMasterUserBySessionId(sessionId);
        OxygenUser buddy = findOxygenUserByDomain(masterBuddy, domainId);

        // Load senior buddy.
        OxygenUser seniorBuddy = loadOxygenUserById(seniorBuddyId, domainId);
        verifyUserByDomain(seniorBuddy, domainId);

        // Ensure that the buddy can teach.
        Map<Long, MissionCompletion> buddyCompletions = buddy.getMissionCompletions();
        if (!buddyCompletions.containsKey(missionId)
                || !buddyCompletions.get(missionId).isStudyComplete()) {
            String buddyName = buddy.getFirstName() + " " + buddy.getLastName();
            throw new BuddyLacksMissionExperienceException(buddyName);
        }

        // Ensure that the senior buddy can supervise.
        Map<Long, MissionCompletion> seniorBuddyCompletions = seniorBuddy.getMissionCompletions();
        if (!seniorBuddy.isAdmin()) {
            if (!seniorBuddyCompletions.containsKey(missionId)
                    || !seniorBuddyCompletions.get(missionId).isMentorCheckoutComplete()) {
                String seniorBuddyName = seniorBuddy.getFirstName() + " " + seniorBuddy.getLastName();
                throw new BuddyLacksMissionExperienceException(seniorBuddyName);
            }
        }

        SendMissionInviteToSeniorBuddyServerAction.sendMissionInviteToSeniorBuddy(
                seniorBuddy.getMasterUserRef().get().getFirebaseToken(),
                student,
                buddy,
                missionLadderId,
                missionTreeId,
                missionId);

        return new UserBean(buddy);
    }
}