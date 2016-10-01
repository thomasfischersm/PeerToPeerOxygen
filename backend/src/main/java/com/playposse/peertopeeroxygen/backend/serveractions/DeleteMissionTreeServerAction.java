package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that deletes a {@link MissionTree}.
 */
public class DeleteMissionTreeServerAction {

    public static void deleteMissionTree(Long missionLadderId, Long missionTreeId)
            throws UnauthorizedException, IOException {

        MissionTree missionTree = ofy().load().type(MissionTree.class).id(missionTreeId).now();
        if (missionTree.getMissions() != null) {
            for (Ref<Mission> missionRef : missionTree.getMissions()) {
                ofy().delete().type(Mission.class).id(missionRef.getKey().getId());
            }
        }

        ofy().delete().type(MissionTree.class).id(missionTree.getId());

        fixLevels(missionLadderId, missionTreeId);

        FirebaseUtil.sendMissionDataInvalidation();
    }

    private static void fixLevels(Long missionLadderId, Long missionTreeId) {
        MissionLadder missionLadder =
                ofy().load().type(MissionLadder.class).id(missionLadderId).now();

        for (int i = 0; i < missionLadder.getMissionTreeRefs().size(); i++) {
            Ref<MissionTree> missionTreeRef = missionLadder.getMissionTreeRefs().get(i);
            if (missionTreeRef.getKey().getId() == missionTreeId) {
                missionLadder.getMissionTreeRefs().remove(missionTreeRef);
                i--;
                continue;
            } else {
                MissionTree missionTree = missionTreeRef.get();
                if (missionTree.getLevel() != i + 1) {
                    missionTree.setLevel(i + 1);
                    ofy().save().entities(missionTree);
                }
            }
        }

        ofy().save().entities(missionLadder);
    }
}
