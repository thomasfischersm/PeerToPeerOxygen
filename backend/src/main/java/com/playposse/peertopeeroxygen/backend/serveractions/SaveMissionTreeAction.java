package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves a {@link MissionTree}.
 */
public class SaveMissionTreeAction {

    private static final Logger log = Logger.getLogger(SaveMissionTreeAction.class.getName());

    public static MissionTreeBean saveMissionTree(
            Long sessionId,
            Long missionLadderId,
            MissionTreeBean missionTreeBean)
            throws UnauthorizedException {

        log.info("saveMissionTree is called (ladder id: " + missionLadderId
                + ", tree id: " + missionTreeBean.getId()
                + ", mission count: " + missionTreeBean.getMissionBeans().size()
                + ", required mission count: " + missionTreeBean.getRequiredMissionIds().size()
                + ")");

        MissionTree missionTree = missionTreeBean.toEntity();
//        ofy().save().entity(missionTree).now();

        if (missionTree.getId() == null) {
            missionTree.setId(factory().allocateId(MissionTree.class).getId());
        }

        if ((missionTree.getMissionBoss() != null)
                && (missionTree.getMissionBoss().getId() == null)) {
            missionTree.getMissionBoss().setId(factory().allocateId(MissionBoss.class).getId());
        }

        MissionLadder missionLadder = ofy().load()
//                .group(MissionTree.class, MissionBoss.class, Mission.class)
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();

        if (missionTreeBean.getId() == null) {
            missionLadder.getMissionTrees().add(missionTree);
        } else {
            for (int i = 0; i < missionLadder.getMissionTrees().size(); i++) {
                if (missionLadder.getMissionTrees().get(i).getId().equals(missionTreeBean.getId())) {
                    missionLadder.getMissionTrees().set(i, missionTree);
                    break;
                }
            }
        }

        log.info("Saving required mission count: " + missionTree.getRequiredMissions().size());
        ofy().save().entity(missionLadder).now();

        return new MissionTreeBean(missionTree);
    }
}
