package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.MissionBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves a {@link Mission}.
 */
public class SaveMissionServerAction extends ServerAction {

    public static MissionBean saveMission(
            Long missionLadderId,
            Long missionTreeId,
            MissionBean missionBean)
            throws UnauthorizedException, IOException {

        Mission mission = missionBean.toEntity();

        MissionLadder missionLadder = ofy().load()
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();

        MissionTree missionTree = findMissionTree(missionLadder, missionTreeId);

        ofy().save().entity(mission).now();

        if (missionBean.getId() == null) {
            missionTree.getMissions().add(Ref.create(Key.create(Mission.class, mission.getId())));
            ofy().save().entity(missionTree).now();
        }

        FirebaseUtil.sendMissionDataInvalidation();

        return new MissionBean(mission);
    }
}
