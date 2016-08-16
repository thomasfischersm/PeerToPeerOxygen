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
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "peerToPeerOxygenApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.peertopeeroxygen.playposse.com",
                ownerName = "backend.peertopeeroxygen.playposse.com",
                packagePath = ""
        )
)
public class PeerToPeerOxygenEndPoint {

    private static final Logger log = Logger.getLogger(PeerToPeerOxygenEndPoint.class.getName());

    /**
     * Retrieves all the mission related data from the server.
     */
    @ApiMethod(name = "getMissionData")
    public CompleteMissionDataBean getMissionData(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        List<OxygenUser> oxygenUsers = ofy()
                .load()
                .type(OxygenUser.class)
                .filter("sessionId", sessionId)
                .list();
        if (oxygenUsers.size() == 0) {
            throw new UnauthorizedException("SessionId is not found: " + sessionId);
        }
        UserBean userBean = new UserBean(oxygenUsers.get(0));

        List<MissionLadder> missionLadders = ofy().load()
                .group(MissionTree.class, Mission.class, MissionBoss.class)
                .type(MissionLadder.class)
                .list();

        return new CompleteMissionDataBean(userBean, missionLadders);
    }

    @ApiMethod(name = "saveMissionLadder")
    public MissionLadderBean saveMissionLadder(
            @Named("sessionId") Long sessionId,
            MissionLadderBean missionLadderBean) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        MissionLadder missionLadder = missionLadderBean.toEntity();
        ofy().save().entity(missionLadder).now();
        return new MissionLadderBean(missionLadder);
    }

    @ApiMethod(name = "deleteMissionLadder")
    public void deleteMissionLadder(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        ofy().delete().type(MissionLadder.class).id(missionLadderId).now();
        log.info("Just deleted mission ladder: " + missionLadderId);
    }

    @ApiMethod(name = "saveMissionTree")
    public MissionTreeBean saveMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            MissionTreeBean missionTreeBean) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

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
                .group(MissionTree.class, MissionBoss.class, Mission.class)
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

    @ApiMethod(name = "saveMission")
    public MissionBean saveMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            MissionBean missionBean)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        Mission mission = missionBean.toEntity();

        MissionLadder missionLadder = ofy().load()
                .group(MissionTree.class, Mission.class)
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();

        MissionTree missionTree = findMissionTree(missionLadder, missionTreeId);

        ofy().save().entity(mission).now();

        if (missionBean.getId() == null) {
            missionTree.getMissions().add(Ref.create(Key.create(Mission.class, mission.getId())));
            ofy().save().entity(missionLadder).now();
        }

        return new MissionBean(mission);
    }

    private MissionTree findMissionTree(MissionLadder missionLadder, Long missionTreeId) {
        for (MissionTree missionTree : missionLadder.getMissionTrees()) {
            if (missionTree.getId().equals(missionTreeId)) {
                return missionTree;
            }
        }
        return null;
    }

    @ApiMethod(name = "deleteMission")
    public void deleteMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        MissionLadder missionLadder = ofy().load()
                .group(MissionTree.class)
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
    }

    private void protectByAdminCheck(Long sessionId) throws UnauthorizedException {
        List<OxygenUser> oxygenUsers = ofy()
                .load()
                .type(OxygenUser.class)
                .filter("sessionId", sessionId)
                .list();
        if (oxygenUsers.size() == 0) {
            throw new UnauthorizedException("SessionId is not found: " + sessionId);
        } else if (!oxygenUsers.get(0).isAdmin()) {
            throw new UnauthorizedException(
                    "The user is NOT an admin: " + oxygenUsers.get(0).getId());
        }
    }

    @ApiMethod(name = "registerOrLogin")
    public UserBean registerOrLogin(@Named("accessToken") String accessToken) {
        Long sessionId = new Random().nextLong();

        // Retrieve user data.
        User fbUser = fetchUserFromFaceBook(accessToken);
        List<OxygenUser> oxygenUsers = ofy()
                .load()
                .type(OxygenUser.class)
                .filter("fbProfileId", fbUser.getId())
                .list();

        // Register if necessary.
        OxygenUser oxygenUser;
        if (oxygenUsers.size() == 0) {
            oxygenUser = new OxygenUser(
                    sessionId,
                    fbUser.getId(),
                    fbUser.getName(),
                    fbUser.getFirstName(),
                    fbUser.getLastName(),
                    fbUser.getPicture().getUrl(),
                    System.currentTimeMillis(),
                    false);
            Key<OxygenUser> oxygenUserKey = ofy().save().entity(oxygenUser).now();
            oxygenUser.setId(oxygenUser.getId());
        } else {
            oxygenUser = oxygenUsers.get(0);
            if (oxygenUsers.size() > 1) {
                log.info("Found more than one OxygenUser entries for fbProfileId: "
                        + fbUser.getId());
            }
            oxygenUser.setSessionId(sessionId);
            oxygenUser.setLastLogin(System.currentTimeMillis());
            ofy().save().entity(oxygenUser).now();
        }

        return new UserBean(oxygenUser);
    }

    private static User fetchUserFromFaceBook(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_7);
        return facebookClient.fetchObject(
                "me",
                User.class,
                Parameter.with("fields", "id,name,link,first_name, last_name,cover,picture.type(large)"));
    }
}
