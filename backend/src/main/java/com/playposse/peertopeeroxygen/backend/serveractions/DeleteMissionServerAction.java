package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionDataInvalidationServerAction;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

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
            Long missionId)
            throws UnauthorizedException, IOException {

        MissionLadder missionLadder = ofy().load()
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();
        MissionTree missionTree = findMissionTree(missionLadder, missionTreeId);
        Key<Mission> missionKey = Key.create(Mission.class, missionId);

        for (Ref<Mission> otherMissionRef : missionTree.getMissions()) {
            if (missionId == otherMissionRef.getKey().getId()) {
                missionTree.getMissions().remove(otherMissionRef);
                ofy().save().entity(missionTree).now();
                break;
            }
        }

        ofy().delete().key(missionKey).now();

        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation();
    }
}
