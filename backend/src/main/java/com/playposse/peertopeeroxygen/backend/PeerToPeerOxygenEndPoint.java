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
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionFeedbackBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionStatsBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.exceptions.BuddyLacksMissionExperienceException;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.serveractions.AddPointsByAdminServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.CheckIntoPracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.CheckOutOfPracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionLadderServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionTreeServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetAllLoanerDevicesServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetAllMissionFeedbackServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetAllMissionStatsServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetMissionDataServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetPracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetStudentRosterServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.InviteBuddyToMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.InviteSeniorBuddyToMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.MarkLoanerDeviceServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.RegisterOrLoginServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ReportMissionCheckoutCompleteServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ReportMissionCompleteServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionLadderServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionTreeServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SavePracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SubmitMissionFeedbackServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.UnmarkLoanerDeviceServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.UpdateFirebaseTokenServerAction;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

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

        return GetMissionDataServerAction.getMissionData(sessionId);
    }

    @ApiMethod(name = "saveMissionLadder")
    public MissionLadderBean saveMissionLadder(
            @Named("sessionId") Long sessionId,
            MissionLadderBean missionLadderBean) throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        return SaveMissionLadderServerAction.saveMissionLadder(sessionId, missionLadderBean);
    }

    @ApiMethod(name = "deleteMissionLadder")
    public void deleteMissionLadder(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId)
            throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        DeleteMissionLadderServerAction.deleteMissionLadder(missionLadderId);
    }

    @ApiMethod(name = "saveMissionTree")
    public MissionTreeBean saveMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            MissionTreeBean missionTreeBean) throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        return SaveMissionTreeServerAction.saveMissionTree(sessionId, missionLadderId, missionTreeBean);
    }

    @ApiMethod(name = "deleteMissionTree")
    public void deleteMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId) throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        DeleteMissionTreeServerAction.deleteMissionTree(missionLadderId, missionTreeId);
    }

    @ApiMethod(name = "saveMission")
    public MissionBean saveMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            MissionBean missionBean)
            throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        return SaveMissionServerAction.saveMission(
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
            throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        DeleteMissionServerAction.deleteMission(sessionId, missionLadderId, missionTreeId, missionId);
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
            @Named("firebaseToken") String firebaseToken,
            @Named("loanerDeviceId") @Nullable Long loanerDeviceId) {

        return RegisterOrLoginServerAction.registerOrLogin(accessToken, firebaseToken, loanerDeviceId);
    }

    @ApiMethod(name = "updateFirebaseToken")
    public void updateFirebaseToken(
            @Named("sessionId") Long sessionId,
            @Named("firebaseToken") String firebaseToken)
            throws UnauthorizedException {

        UpdateFirebaseTokenServerAction.updateFirebaseToken(sessionId, firebaseToken);
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

        return InviteBuddyToMissionServerAction.inviteBuddyToMission(
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

        return InviteSeniorBuddyToMissionServerAction.inviteSeniorBuddyToMission(
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

        ReportMissionCompleteServerAction.reportMissionComplete(sessionId, studentId, missionId);
    }

    @ApiMethod(name = "reportMissionCheckoutComplete")
    public void reportMissionCheckoutComplete(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("buddyId") Long buddyId,
            @Named("missionId") Long missionId)
            throws UnauthorizedException, IOException {

        ReportMissionCheckoutCompleteServerAction
                .reportMissionCheckoutComplete(sessionId, studentId, buddyId, missionId);
    }

    @ApiMethod(name = "getStudentRoster")
    public List<UserBean> getStudentRoster(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return new GetStudentRosterServerAction().getStudentRoster();
    }

    @ApiMethod(name = "addPointsByAdmin")
    public void addPointsByAdmin(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("pointType") String pointType,
            @Named("addedPoints") int addedPoints)
            throws UnauthorizedException, IOException {

        OxygenUser adminUser = protectByAdminCheck(sessionId);

        new AddPointsByAdminServerAction().addPointsByAdmin(adminUser, studentId, pointType, addedPoints);
    }

    @ApiMethod(name = "submitMissionFeedback")
    public void submitMissionFeedback(
            @Named("sessionId") Long sessionId,
            @Named("missionId") Long missionId,
            @Named("rating") int rating,
            @Named("comment") @Nullable String comment) throws UnauthorizedException {

        new SubmitMissionFeedbackServerAction()
                .submitMissionFeedback(sessionId, missionId, rating, comment);
    }

    @ApiMethod(name = "getAllMissionFeedback")
    public List<MissionFeedbackBean> getAllMissionFeedback(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return GetAllMissionFeedbackServerAction.getAllMissionFeedback();
    }

    @ApiMethod(name = "getAllMissionStats")
    public List<MissionStatsBean> getAllMissionStats(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return GetAllMissionStatsServerAction.getAllMissionStats();
    }

    @ApiMethod(name = "markLoanerDevice")
    public LoanerDeviceBean markLoanerDevice(
            @Named("sessionId") Long sessionId,
            @Named("friendlyName") String friendlyName)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return MarkLoanerDeviceServerAction.markLoanerDevice(sessionId, friendlyName);
    }

    @ApiMethod(name = "unmarkLoanerDevice")
    public void unmarkLoanerDevice(
            @Named("sessionId") Long sessionId,
            @Named("loanerDeviceId") Long loanerDeviceId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        UnmarkLoanerDeviceServerAction.unmarkLoanerDevice(loanerDeviceId);
    }

    @ApiMethod(name = "getAllLoanerDevices")
    public List<LoanerDeviceBean> getAllLoanerDevices(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        protectByAdminCheck(sessionId);

        return GetAllLoanerDevicesServerAction.getAllLoanerDevices();
    }

    @ApiMethod(name = "savePractica")
    public PracticaBean savePractica(
            @Named("sessionId") Long sessionId,
            PracticaBean practicaBean)
            throws UnauthorizedException, IOException {

        protectByAdminCheck(sessionId);

        return SavePracticaServerAction.save(practicaBean);
    }

    @ApiMethod(name = "getPractica")
    public List<PracticaBean> getPractica(
            @Named("practicaDates") GetPracticaServerAction.PracticaDates practicaDates)
            throws BadRequestException {

        return GetPracticaServerAction.getPractica(practicaDates);
    }

    @ApiMethod(name = "checkIntoPractica")
    public PracticaBean checkIntoPractica(
            @Named("sessionId") Long sessionId,
            @Named("practicaId") Long practicaId)
            throws UnauthorizedException, BadRequestException, IOException {

        return CheckIntoPracticaServerAction.checkin(sessionId, practicaId);
    }

    @ApiMethod(name = "checkOutOfPractica")
    public void checkOutOfPractica(
            @Named("sessionId") Long sessionId,
            @Named("practicaId") Long practicaId)
            throws UnauthorizedException, BadRequestException, IOException {

        CheckOutOfPracticaServerAction.checkout(sessionId, practicaId);
    }
}
