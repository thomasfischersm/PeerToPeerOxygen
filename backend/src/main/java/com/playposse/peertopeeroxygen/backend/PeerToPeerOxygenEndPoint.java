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
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.exceptions.BuddyLacksMissionExperienceException;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.serveractions.AddPointsByAdminAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionLadderAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionTreeAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetMissionDataAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetStudentRosterAction;
import com.playposse.peertopeeroxygen.backend.serveractions.InviteBuddyToMissionAction;
import com.playposse.peertopeeroxygen.backend.serveractions.InviteSeniorBuddyToMissionAction;
import com.playposse.peertopeeroxygen.backend.serveractions.RegisterOrLoginAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ReportMissionCheckoutCompleteAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ReportMissionCompleteAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionLadderAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionTreeAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.UpdateFirebaseTokenAction;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

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

        return GetMissionDataAction.getMissionData(sessionId);
    }

    @ApiMethod(name = "saveMissionLadder")
    public MissionLadderBean saveMissionLadder(
            @Named("sessionId") Long sessionId,
            MissionLadderBean missionLadderBean) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return SaveMissionLadderAction.saveMissionLadder(sessionId, missionLadderBean);
    }

    @ApiMethod(name = "deleteMissionLadder")
    public void deleteMissionLadder(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        DeleteMissionLadderAction.deleteMissionLadder(missionLadderId);
    }

    @ApiMethod(name = "saveMissionTree")
    public MissionTreeBean saveMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            MissionTreeBean missionTreeBean) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return SaveMissionTreeAction.saveMissionTree(sessionId, missionLadderId, missionTreeBean);
    }

    @ApiMethod(name = "deleteMissionTree")
    public void deleteMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionTreeId") Long missionTreeId) throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        DeleteMissionTreeAction.deleteMissionTree(missionTreeId);
    }

    @ApiMethod(name = "saveMission")
    public MissionBean saveMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            MissionBean missionBean)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return SaveMissionAction.saveMission(
                sessionId,
                missionLadderId,
                missionTreeId,
                missionBean);
    }

    @ApiMethod(name = "deleteMission")
    public void deleteMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        DeleteMissionAction.deleteMission(sessionId, missionLadderId, missionTreeId, missionId);
    }

    private OxygenUser protectByAdminCheck(Long sessionId) throws UnauthorizedException {
        OxygenUser oxygenUser = ServerAction.loadUserBySessionId(sessionId);
        if (!oxygenUser.isAdmin()) {
            throw new UnauthorizedException(
                    "The user is NOT an admin: " + oxygenUser.getId());
        }
        return oxygenUser;
    }

    @ApiMethod(name = "registerOrLogin")
    public UserBean registerOrLogin(
            @Named("accessToken") String accessToken,
            @Named("firebaseToken") String firebaseToken) {

        return RegisterOrLoginAction.registerOrLogin(accessToken, firebaseToken);
    }

    @ApiMethod(name = "updateFirebaseToken")
    public void updateFirebaseToken(
            @Named("sessionId") Long sessionId,
            @Named("firebaseToken") String firebaseToken)
            throws UnauthorizedException {

        UpdateFirebaseTokenAction.updateFirebaseToken(sessionId, firebaseToken);
    }

    /**
     * Invites a buddy to teach a mission. The buddy is sent a message via Firebase.
     *
     * @return UserBean Information about the buddy.
     */
    @ApiMethod(name = "inviteBuddyToMission")
    public UserBean inviteBuddyToMission(
            @Named("sessionId") Long sessionId,
            @Named("buddyId") Long buddyId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException, IOException, BuddyLacksMissionExperienceException {

        return InviteBuddyToMissionAction.inviteBuddyToMission(
                sessionId,
                buddyId,
                missionLadderId,
                missionTreeId,
                missionId);
    }


    /**
     * Invites a senior buddy to supervise the teaching of a mission. The senior buddy is sent a
     * message via Firebase.
     *
     * @return UserBean Information about the buddy.
     */
    @ApiMethod(name = "inviteSeniorBuddyToMission")
    public UserBean inviteSeniorBuddyToMission(
            @Named("sessionId") Long sessionId,
            @Named("buddyId") Long studentId,
            @Named("seniorBuddyId") Long seniorBuddyId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException, IOException, BuddyLacksMissionExperienceException {

        return InviteSeniorBuddyToMissionAction.inviteSeniorBuddyToMission(
                sessionId,
                studentId,
                seniorBuddyId,
                missionLadderId,
                missionTreeId,
                missionId);
    }

    @ApiMethod(name = "reportMissionComplete")
    public void reportMissionComplete(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException, IOException {

        ReportMissionCompleteAction.reportMissionComplete(sessionId, studentId, missionId);
    }

    @ApiMethod(name = "reportMissionCheckoutComplete")
    public void reportMissionCheckoutComplete(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("buddyId") Long buddyId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException, IOException {

        ReportMissionCheckoutCompleteAction
                .reportMissionCheckoutComplete(sessionId, studentId, buddyId, missionId);
    }

    @ApiMethod(name = "getStudentRoster")
    public List<UserBean> getStudentRoster(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return new GetStudentRosterAction().getStudentRoster();
    }

    @ApiMethod(name = "addPointsByAdmin")
    public void addPointsByAdmin(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("pointType") String pointType,
            @Named("addedPoints") int addedPoints)
            throws UnauthorizedException, IOException {

        OxygenUser adminUser = protectByAdminCheck(sessionId);

        new AddPointsByAdminAction().addPointsByAdmin(adminUser, studentId, pointType, addedPoints);
    }
}
