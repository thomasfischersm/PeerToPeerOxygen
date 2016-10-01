package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that downloads all the mission data. The Android client calls this on startup.
 */
public class GetMissionDataServerAction {

    public static CompleteMissionDataBean getMissionData( Long sessionId)
            throws UnauthorizedException {

        List<OxygenUser> oxygenUsers = ofy()
                .load()
//                .group(UserPoints.class)
                .type(OxygenUser.class)
                .filter("sessionId", sessionId)
                .list();
        if (oxygenUsers.size() == 0) {
            throw new UnauthorizedException("SessionId is not found: " + sessionId);
        }
        UserBean userBean = new UserBean(oxygenUsers.get(0));

        List<MissionLadder> missionLadders = ofy().load()
//                .group(MissionTree.class, Mission.class, MissionBoss.class, UserPoints.class)
                .type(MissionLadder.class)
                .list();

        return new CompleteMissionDataBean(userBean, missionLadders);
    }
}
