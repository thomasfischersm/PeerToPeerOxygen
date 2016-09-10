package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves the mission ladder.
 */
public class SaveMissionLadderAction {

    public static MissionLadderBean saveMissionLadder(
            Long sessionId,
            MissionLadderBean missionLadderBean)
            throws UnauthorizedException, IOException {

        MissionLadder missionLadder = missionLadderBean.toEntity();
        ofy().save().entity(missionLadder).now();

        FirebaseUtil.sendMissionDataInvalidation();

        return new MissionLadderBean(missionLadder);
    }
}
