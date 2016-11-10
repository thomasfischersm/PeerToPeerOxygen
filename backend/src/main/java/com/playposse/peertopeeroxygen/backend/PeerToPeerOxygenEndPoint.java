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
import com.googlecode.objectify.ObjectifyService;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.beans.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionFeedbackBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionStatsBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.exceptions.BuddyLacksMissionExperienceException;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Practica;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;
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
import com.playposse.peertopeeroxygen.backend.serveractions.GetPracticaByIdServerAction;
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
import com.playposse.peertopeeroxygen.backend.util.ObjectifyRegistrationServletContextListener;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

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
    public CompleteMissionDataBean getMissionData(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {
//ObjectifyRegistrationServletContextListener.bootStrapFirstDomain(); // DELETE
//ofy().load().type(MissionLadder.class).list().size(); // DELETE
//        ofy().load().type(Domain.class).list().size(); // DELETE
//        ofy().load().type(LevelCompletion.class).list().size(); // DELETE
//        ofy().load().type(LoanerDevice.class).list().size(); // DELETE
//        ofy().load().type(MasterUser.class).list().size(); // DELETE
//        ofy().load().type(MentoringAuditLog.class).list().size(); // DELETE
//        ofy().load().type(Mission.class).list().size(); // DELETE
//        ofy().load().type(MissionCompletion.class).list().size(); // DELETE
//        ofy().load().type(MissionFeedback.class).list().size(); // DELETE
//        ofy().load().type(MissionLadder.class).list().size(); // DELETE
//        ofy().load().type(MissionStats.class).list().size(); // DELETE
//        ofy().load().type(MissionTree.class).list().size(); // DELETE
//        ofy().load().type(OxygenUser.class).list().size(); // DELETE
//        ofy().load().type(PointsTransferAuditLog.class).list().size(); // DELETE
//        ofy().load().type(Practica.class).list().size(); // DELETE
//        ofy().load().type(UserPoints.class).list().size(); // DELETE
        return GetMissionDataServerAction.getMissionData(sessionId, domainId);
    }

    @ApiMethod(name = "saveMissionLadder")
    public MissionLadderBean saveMissionLadder(
            @Named("sessionId") Long sessionId,
            MissionLadderBean missionLadderBean,
            @Named("domainId2") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        return SaveMissionLadderServerAction
                .saveMissionLadder(sessionId, missionLadderBean, domainId);
    }

    @ApiMethod(name = "deleteMissionLadder")
    public void deleteMissionLadder(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        DeleteMissionLadderServerAction.deleteMissionLadder(sessionId, missionLadderId, domainId);
    }

    @ApiMethod(name = "saveMissionTree")
    public MissionTreeBean saveMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            MissionTreeBean missionTreeBean,
            @Named("domainId2") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        return SaveMissionTreeServerAction
                .saveMissionTree(sessionId, missionLadderId, missionTreeBean, domainId);
    }

    @ApiMethod(name = "deleteMissionTree")
    public void deleteMissionTree(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        DeleteMissionTreeServerAction
                .deleteMissionTree(sessionId, missionLadderId, missionTreeId, domainId);
    }

    @ApiMethod(name = "saveMission")
    public MissionBean saveMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            MissionBean missionBean,
            @Named("domainId2") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        return SaveMissionServerAction.saveMission(
                sessionId,
                missionLadderId,
                missionTreeId,
                missionBean,
                domainId);
    }

    @ApiMethod(name = "deleteMission")
    public void deleteMission(
            @Named("sessionId") Long sessionId,
            @Named("missionLadderId") Long missionLadderId,
            @Named("missionTreeId") Long missionTreeId,
            @Named("missionId") Long missionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        DeleteMissionServerAction
                .deleteMission(sessionId, missionLadderId, missionTreeId, missionId, domainId);
    }

    @ApiMethod(name = "registerOrLogin")
    public MasterUserBean registerOrLogin(
            @Named("accessToken") String accessToken,
            @Named("firebaseToken") String firebaseToken,
            @Named("loanerDeviceId") @Nullable Long loanerDeviceId,
            @Named("domainId") @Nullable Long domainId) {

        return RegisterOrLoginServerAction
                .registerOrLogin(accessToken, firebaseToken, loanerDeviceId, domainId);
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
            @Named("missionId") Long missionId,
            @Named("domainId") Long domainId)
            throws
            UnauthorizedException,
            IOException,
            BuddyLacksMissionExperienceException,
            BadRequestException {

        return InviteBuddyToMissionServerAction.inviteBuddyToMission(
                sessionId,
                buddyId,
                missionLadderId,
                missionTreeId,
                missionId,
                domainId);
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
            @Named("missionId") Long missionId,
            @Named("domainId") Long domainId)
            throws
            UnauthorizedException,
            IOException,
            BuddyLacksMissionExperienceException,
            BadRequestException {

        return InviteSeniorBuddyToMissionServerAction.inviteSeniorBuddyToMission(
                sessionId,
                studentId,
                seniorBuddyId,
                missionLadderId,
                missionTreeId,
                missionId,
                domainId);
    }

    @ApiMethod(name = "reportMissionComplete")
    public void reportMissionComplete(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("missionId") Long missionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        ReportMissionCompleteServerAction
                .reportMissionComplete(sessionId, studentId, missionId, domainId);
    }

    @ApiMethod(name = "reportMissionCheckoutComplete")
    public void reportMissionCheckoutComplete(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("buddyId") Long buddyId,
            @Named("missionId") Long missionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        ReportMissionCheckoutCompleteServerAction
                .reportMissionCheckoutComplete(sessionId, studentId, buddyId, missionId, domainId);
    }

    @ApiMethod(name = "getStudentRoster")
    public List<UserBean> getStudentRoster(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        return new GetStudentRosterServerAction().getStudentRoster(sessionId, domainId);
    }

    @ApiMethod(name = "addPointsByAdmin")
    public void addPointsByAdmin(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("pointType") String pointType,
            @Named("addedPoints") int addedPoints,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        new AddPointsByAdminServerAction()
                .addPointsByAdmin(sessionId, studentId, pointType, addedPoints, domainId);
    }

    @ApiMethod(name = "submitMissionFeedback")
    public void submitMissionFeedback(
            @Named("sessionId") Long sessionId,
            @Named("missionId") Long missionId,
            @Named("rating") int rating,
            @Named("comment") @Nullable String comment,
            @Named("domainId") Long domainId) throws UnauthorizedException, BadRequestException {

        new SubmitMissionFeedbackServerAction()
                .submitMissionFeedback(sessionId, missionId, rating, comment, domainId);
    }

    @ApiMethod(name = "getAllMissionFeedback")
    public List<MissionFeedbackBean> getAllMissionFeedback(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        return GetAllMissionFeedbackServerAction.getAllMissionFeedback(sessionId, domainId);
    }

    @ApiMethod(name = "getAllMissionStats")
    public List<MissionStatsBean> getAllMissionStats(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        return GetAllMissionStatsServerAction.getAllMissionStats(sessionId, domainId);
    }

    @ApiMethod(name = "markLoanerDevice")
    public LoanerDeviceBean markLoanerDevice(
            @Named("sessionId") Long sessionId,
            @Named("friendlyName") String friendlyName,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        return MarkLoanerDeviceServerAction.markLoanerDevice(sessionId, friendlyName, domainId);
    }

    @ApiMethod(name = "unmarkLoanerDevice")
    public void unmarkLoanerDevice(
            @Named("sessionId") Long sessionId,
            @Named("loanerDeviceId") Long loanerDeviceId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        UnmarkLoanerDeviceServerAction.unmarkLoanerDevice(sessionId, loanerDeviceId, domainId);
    }

    @ApiMethod(name = "getAllLoanerDevices")
    public List<LoanerDeviceBean> getAllLoanerDevices(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        return GetAllLoanerDevicesServerAction.getAllLoanerDevices(sessionId, domainId);
    }

    @ApiMethod(name = "savePractica")
    public PracticaBean savePractica(
            @Named("sessionId") Long sessionId,
            PracticaBean practicaBean,
            @Named("domainId2") Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        return SavePracticaServerAction.save(sessionId, practicaBean, domainId);
    }

    @ApiMethod(name = "getPractica")
    public List<PracticaBean> getPractica(
            @Named("sessionId") Long sessionId,
            @Named("practicaDates") GetPracticaServerAction.PracticaDates practicaDates,
            @Named("domainId") Long domainId)
            throws BadRequestException, UnauthorizedException {

        return GetPracticaServerAction.getPractica(sessionId, practicaDates, domainId);
    }

    @ApiMethod(name = "getPracticaById")
    public PracticaBean getPracticaById(
            @Named("sessionId") Long sessionId,
            @Named("practicaId") Long practicaId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

        return GetPracticaByIdServerAction.getPracticaById(sessionId, practicaId, domainId);
    }

    @ApiMethod(name = "checkIntoPractica")
    public PracticaBean checkIntoPractica(
            @Named("sessionId") Long sessionId,
            @Named("practicaId") Long practicaId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException, IOException {

        return CheckIntoPracticaServerAction.checkin(sessionId, practicaId, domainId);
    }

    @ApiMethod(name = "checkOutOfPractica")
    public void checkOutOfPractica(
            @Named("sessionId") Long sessionId,
            @Named("practicaId") Long practicaId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException, IOException {

        CheckOutOfPracticaServerAction.checkout(sessionId, practicaId, domainId);
    }
}
