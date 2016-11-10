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
 * An action that deletes a {@link MissionLadder}.
 */
public class DeleteMissionLadderServerAction extends ServerAction {

    public static void deleteMissionLadder(Long sessionId, Long missionLadderId, Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Look up data
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        MissionLadder missionLadder =
                ofy().load().type(MissionLadder.class).id(missionLadderId).now();

        // Do security checks.
        protectByAdminCheck(masterUser, oxygenUser, domainId);
        verifyMissionLadderByDomain(missionLadder, domainId);

        if (missionLadder.getMissionTreeRefs() != null) {
            for (Ref<MissionTree> missionTreeRef : missionLadder.getMissionTreeRefs()) {
                MissionTree missionTree = missionTreeRef.get();
                if (missionTree.getMissions() != null) {
                    for (Ref<Mission> missionRef : missionTree.getMissions()) {
                        ofy().delete().type(Mission.class).id(missionRef.getKey().getId());
                    }
                }
                ofy().delete().type(MissionTree.class).id(missionTree.getId());
            }
        }

        ofy().delete().type(MissionLadder.class).id(missionLadderId);

        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation(domainId);
    }
}
