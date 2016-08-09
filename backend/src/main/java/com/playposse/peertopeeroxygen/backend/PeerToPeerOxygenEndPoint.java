/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.playposse.peertopeeroxygen.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.util.List;
import java.util.logging.Logger;

import sun.rmi.runtime.Log;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/** An endpoint class we are exposing */
@Api(
  name = "peerToPeerOxygenApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.peertopeeroxygen.playposse.com",
    ownerName = "backend.peertopeeroxygen.playposse.com",
    packagePath=""
  )
)
public class PeerToPeerOxygenEndPoint {

    private static final Logger log = Logger.getLogger(PeerToPeerOxygenEndPoint.class.getName());

    /**
     * Retrieves all the mission related data from the server.
     */
    @ApiMethod(name = "getMissionData")
    public CompleteMissionDataBean getMissionData() {
        List<MissionLadder> missionLadders = ofy().load()
                .group(MissionTree.class, Mission.class, MissionBoss.class)
                .type(MissionLadder.class)
                .list();

        return new CompleteMissionDataBean(missionLadders);
    }

    @ApiMethod(name = "saveMissionLadder")
    public MissionLadderBean saveMissionLadder(MissionLadderBean missionLadderBean) {
        MissionLadder missionLadder = missionLadderBean.toEntity();
        ofy().save().entity(missionLadder).now();
        return new MissionLadderBean(missionLadder);
    }

    @ApiMethod(name = "deleteMissionLadder")
    public void deleteMissionLadder(@Named("missionLadderId") Long missionLadderId) {
        ofy().delete().type(MissionLadder.class).id(missionLadderId).now();
        log.info("Just deleted mission ladder: " + missionLadderId);
    }

    @ApiMethod(name = "saveMissionTree")
    public MissionTreeBean saveMissionTree(
            @Named("missionLadderId") Long missionLadderId,
            MissionTreeBean missionTreeBean) {

        log.info("saveMissionTree is called (ladder id: " + missionLadderId
                + ", tree id: " + missionTreeBean.getId() + ")");

        MissionTree missionTree = missionTreeBean.toEntity();
//        ofy().save().entity(missionTree).now();

        if (missionTree.getId() == null) {
            missionTree.setId(factory().allocateId(MissionTree.class).getId());
        }

        MissionLadder missionLadder = ofy().load()
                .group(MissionTree.class)
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();

        if (missionTreeBean.getId() == null) {
            missionLadder.getMissionTrees().add(missionTree);
        } else {
            for (int i = 0; i < missionLadder.getMissionTrees().size(); i++) {
                if (missionLadder.getMissionTrees().get(i).getId().equals(missionTreeBean.getId())) {
                    missionLadder.getMissionTrees().set(i, missionTree);
                }
            }
        }

        ofy().save().entity(missionLadder).now();

        return new MissionTreeBean(missionTree);
    }
}
