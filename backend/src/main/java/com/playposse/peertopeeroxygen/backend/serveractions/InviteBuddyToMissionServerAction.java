package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.exceptions.BuddyLacksMissionExperienceException;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionInviteToBuddyServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;
import java.util.Map;

/**
 * A server action that invites a buddy to a mission. This will fire a Firebase message to notify
 * the Android device of the buddy.
 */
public class InviteBuddyToMissionServerAction extends ServerAction {

    public static UserBean inviteBuddyToMission(
            Long sessionId,
            Long buddyId,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId,
            Long domainId)
            throws UnauthorizedException, IOException, BuddyLacksMissionExperienceException, BadRequestException {

        // Load and verify student.
        MasterUser masterStudent = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenStudent = findOxygenUserByDomain(masterStudent, domainId);
        protectByAdminCheck(masterStudent, oxygenStudent, domainId);

        // Load and verify buddy
        OxygenUser buddy = loadOxygenUserById(buddyId, domainId);
        verifyUserByDomain(buddy, domainId);

        Map<Long, MissionCompletion> buddyCompletions = buddy.getMissionCompletions();
        if (!buddy.isAdmin()) {
            if (!buddyCompletions.containsKey(missionId)
                    || !buddyCompletions.get(missionId).isStudyComplete()) {
                String buddyName = buddy.getFirstName() + " " + buddy.getLastName();
                throw new BuddyLacksMissionExperienceException(buddyName);
            }
        }

        SendMissionInviteToBuddyServerAction.sendMissionInviteToBuddy(
                buddy.getMasterUserRef().get().getFirebaseToken(),
                oxygenStudent,
                missionLadderId,
                missionTreeId,
                missionId);

        return new UserBean(buddy);
    }
}
