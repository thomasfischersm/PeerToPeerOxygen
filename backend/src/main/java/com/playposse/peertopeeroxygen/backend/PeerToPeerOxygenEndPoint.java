/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.playposse.peertopeeroxygen.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.util.List;

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

    /**
     * Retrieves all the mission related data from the server.
     */
    @ApiMethod(name = "sayHi")
    public MissionDataBean getMissionData() {
        List<MissionLadder> missionLadders = ofy().load()
                .group(MissionTree.class, Mission.class, MissionBoss.class)
                .type(MissionLadder.class)
                .list();

        return new MissionDataBean();
    }

}
