package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
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
 * A server action that deletes a {@link Mission}.
 */
public class DeleteMissionServerAction extends ServerAction {

    public static void deleteMission(
            Long sessionId,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Look up data
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        MissionLadder missionLadder = ofy().load()
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();
        MissionTree missionTree = findMissionTree(missionLadder, missionTreeId);
        Key<Mission> missionKey = Key.create(Mission.class, missionId);

        // Do security checks.
        protectByAdminCheck(masterUser, oxygenUser, domainId);
        verifyMissionLadderByDomain(missionLadder, domainId);
        verifyMissionTreeByDomain(missionTree, domainId);

        for (Ref<Mission> otherMissionRef : missionTree.getMissions()) {
            if (missionId == otherMissionRef.getKey().getId()) {
                missionTree.getMissions().remove(otherMissionRef);
                ofy().save().entity(missionTree).now();
                break;
            }
        }

        ofy().delete().key(missionKey).now();

        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation(domainId);
    }
}
