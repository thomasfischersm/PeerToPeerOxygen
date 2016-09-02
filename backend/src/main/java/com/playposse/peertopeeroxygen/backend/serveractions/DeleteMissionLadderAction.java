package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that deletes a {@link MissionLadder}.
 */
public class DeleteMissionLadderAction {

    public static void deleteMissionLadder(Long missionLadderId) throws UnauthorizedException {
        MissionLadder missionLadder =
                ofy().load().type(MissionLadder.class).id(missionLadderId).now();

        if (missionLadder.getMissionTreeRefs() != null) {
            for (Ref<MissionTree> missionTreeRef : missionLadder.getMissionTreeRefs()) {
                MissionTree missionTree = missionTreeRef.getValue();
                if (missionTree.getMissions() != null) {
                    for (Ref<Mission> missionRef : missionTree.getMissions()) {
                        ofy().delete().type(Mission.class).id(missionRef.getKey().getId());
                    }
                }
                ofy().delete().type(MissionTree.class).id(missionTree.getId());
            }
        }

        ofy().delete().type(MissionLadder.class).id(missionLadderId);
    }
}
