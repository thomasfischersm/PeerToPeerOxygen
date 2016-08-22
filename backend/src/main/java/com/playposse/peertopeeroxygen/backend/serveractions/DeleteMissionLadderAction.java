package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that deletes a {@link MissionLadder}.
 */
public class DeleteMissionLadderAction {

    public static void deleteMissionLadder(
            Long sessionId,
            Long missionLadderId) throws UnauthorizedException {

        ofy().delete().type(MissionLadder.class).id(missionLadderId).now();
    }
}
