package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionDataInvalidationServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that deletes a {@link MissionTree}.
 */
public class DeleteMissionTreeServerAction extends ServerAction {

    public static void deleteMissionTree(
            Long sessionId,
            Long missionLadderId,
            Long missionTreeId,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Look up data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        MissionTree missionTree = ofy().load().type(MissionTree.class).id(missionTreeId).now();

        // Do security checks
        protectByAdminCheck(masterUser, oxygenUser, domainId);
        verifyMissionTreeByDomain(missionTree, domainId);

        if (missionTree.getMissions() != null) {
            for (Ref<Mission> missionRef : missionTree.getMissions()) {
                ofy().delete().type(Mission.class).id(missionRef.getKey().getId());
            }
        }

        ofy().delete().type(MissionTree.class).id(missionTree.getId());

        fixLevels(missionLadderId, missionTreeId);

        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation(domainId);
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
