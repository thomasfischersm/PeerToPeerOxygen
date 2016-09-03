package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.exceptions.BuddyLacksMissionExperienceException;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;
import java.util.Map;

/**
 * A server action that invites a buddy to a mission. This will fire a Firebase message to notify
 * the Android device of the buddy.
 */
public class InviteBuddyToMissionAction extends ServerAction {

    public static UserBean inviteBuddyToMission(
            Long sessionId,
            Long buddyId,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId)
            throws UnauthorizedException, IOException, BuddyLacksMissionExperienceException {

        OxygenUser student = loadUserBySessionId(sessionId);
        OxygenUser buddy = loadUserById(buddyId);

        Map<Long, MissionCompletion> buddyCompletions = buddy.getMissionCompletions();
        if (!buddy.isAdmin()) {
            if (!buddyCompletions.containsKey(missionId)
                    || !buddyCompletions.get(missionId).isStudyComplete()) {
                String buddyName = buddy.getFirstName() + " " + buddy.getLastName();
                throw new BuddyLacksMissionExperienceException(buddyName);
            }
        }

        FirebaseUtil.sendMissionInviteToBuddy(
                buddy.getFirebaseToken(),
                student,
                missionLadderId,
                missionTreeId,
                missionId);

        return stripForSafety(new UserBean(buddy));
    }
}
