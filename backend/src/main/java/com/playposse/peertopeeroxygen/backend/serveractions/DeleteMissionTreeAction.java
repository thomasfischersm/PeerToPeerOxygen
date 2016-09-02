package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that deletes a {@link MissionTree}.
 */
public class DeleteMissionTreeAction {

    public static void deleteMissionTree(Long missionTreeId) throws UnauthorizedException {
        MissionTree missionTree = ofy().load().type(MissionTree.class).id(missionTreeId).now();
        if (missionTree.getMissions() != null) {
            for (Ref<Mission> missionRef : missionTree.getMissions()) {
                ofy().delete().type(Mission.class).id(missionRef.getKey().getId());
            }
        }

        ofy().delete().type(MissionTree.class).id(missionTree.getId());
    }
}
