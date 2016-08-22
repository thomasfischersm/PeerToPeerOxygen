package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;

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
            throws UnauthorizedException, IOException {

        OxygenUser student = loadUserBySessionId(sessionId);
        OxygenUser buddy = loadUserById(buddyId);
//        Mission mission = ofy().load().type(Mission.class).id(missionId).now();

        // TODO: Check if the buddy is allowed to teach the mission.

        FirebaseUtil.sendMissionInviteToBuddy(
                buddy.getFirebaseToken(),
                stripForSafety(new UserBean(student)),
                missionLadderId,
                missionTreeId,
                missionId);

        return stripForSafety(new UserBean(buddy));
    }
}
