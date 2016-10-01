package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.io.IOException;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves a {@link MissionTree}.
 */
public class SaveMissionTreeServerAction {

    private static final Logger log = Logger.getLogger(SaveMissionTreeServerAction.class.getName());

    public static MissionTreeBean saveMissionTree(
            Long sessionId,
            Long missionLadderId,
            MissionTreeBean missionTreeBean)
            throws UnauthorizedException, IOException {

        log.info("saveMissionTree is called (ladder id: " + missionLadderId
                + ", tree id: " + missionTreeBean.getId()
                + ", mission count: " + missionTreeBean.getMissionBeans().size()
                + ", required mission count: " + missionTreeBean.getRequiredMissionIds().size()
                + ")");

        MissionTree missionTree = missionTreeBean.toEntity();
        ofy().save().entity(missionTree).now();

        MissionLadder missionLadder = ofy().load()
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();

        boolean needsToBeAdded = true;
        if (missionLadder.getMissionTreeRefs() != null) {
            for (Ref<MissionTree> otherMissionTreeRef : missionLadder.getMissionTreeRefs()) {
                if (missionTree.getId().equals(otherMissionTreeRef.getKey().getId())) {
                    needsToBeAdded = false;
                    break;
                }
            }
            if (needsToBeAdded) {
                Ref<MissionTree> missionTreeRef =
                        Ref.create(Key.create(MissionTree.class, missionTree.getId()));
                missionLadder.getMissionTreeRefs().add(missionTreeRef);
                ofy().save().entity(missionLadder).now();
            }
        }

        log.info("Saving required mission count: " + missionTree.getRequiredMissions().size());

        FirebaseUtil.sendMissionDataInvalidation();

        return new MissionTreeBean(missionTree);
    }
}
