package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves the mission ladder.
 */
public class SaveMissionLadderAction {

    public static MissionLadderBean saveMissionLadder(
            Long sessionId,
            MissionLadderBean missionLadderBean)
            throws UnauthorizedException {

        MissionLadder missionLadder = missionLadderBean.toEntity();
        ofy().save().entity(missionLadder).now();
        return new MissionLadderBean(missionLadder);
    }
}
