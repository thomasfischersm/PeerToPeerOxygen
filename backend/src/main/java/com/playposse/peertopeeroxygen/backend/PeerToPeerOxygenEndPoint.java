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
import com.playposse.peertopeeroxygen.backend.beans.CombinedDomainBeans;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
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
import com.playposse.peertopeeroxygen.backend.exceptions.DuplicateDomainNameException;
import com.playposse.peertopeeroxygen.backend.serveractions.AddPointsByAdminServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.CheckIntoPracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.CheckOutOfPracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.CleanTestDataServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.CreatePrivateDomainServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionLadderServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.DeleteMissionTreeServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetAllLoanerDevicesServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetAllMissionFeedbackServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetAllMissionStatsServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetMissionDataServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetPracticaByIdServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetPracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetPublicDomainsServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.GetStudentRosterServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.InviteBuddyToMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.InviteSeniorBuddyToMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.MarkLoanerDeviceServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.PromoteToAdminServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.RegisterOrLoginServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ReportMissionCheckoutCompleteServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.ReportMissionCompleteServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveDomainServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionLadderServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SaveMissionTreeServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SavePracticaServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SubmitMissionFeedbackServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.SubscribeToDomainServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.UnmarkLoanerDeviceServerAction;
import com.playposse.peertopeeroxygen.backend.serveractions.UpdateFirebaseTokenServerAction;

import java.io.IOException;
import java.util.List;
import java.util.Set;
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

    @ApiMethod(name="getTest")
    public void getTest() {
        log.info("happy call");
    }

    /**
     * Retrieves all the mission related data from the server.
     */
    @ApiMethod(name = "getMissionData")
    public CompleteMissionDataBean getMissionData(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws UnauthorizedException, BadRequestException {

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
            @Named("domainId") @Nullable Long domainId) throws BadRequestException {

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

    @ApiMethod(name = "getPractica", path="getPractica", httpMethod = ApiMethod.HttpMethod.POST)
    public List<PracticaBean> getPractica(
            @Named("sessionId") Long sessionId,
            @Named("practicaDates") GetPracticaServerAction.PracticaDates practicaDates,
            @Named("domainIds") Set<Long> domainIds)
            throws BadRequestException, UnauthorizedException {

        return GetPracticaServerAction.getPractica(sessionId, practicaDates, domainIds);
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

    @ApiMethod(name = "getPublicDomains")
    public CombinedDomainBeans getPublicDomains(@Named("sessionId") Long sessionId)
            throws UnauthorizedException {

        return GetPublicDomainsServerAction.getPublicDomains(sessionId);
    }

    @ApiMethod(name = "promoteToAdmin")
    public void promoteToAdmin(
            @Named("sessionId") Long sessionId,
            @Named("studentId") Long studentId,
            @Named("domainId") Long domainId,
            @Named("isAdmin") boolean isAdmin)
            throws UnauthorizedException, BadRequestException, IOException {

        PromoteToAdminServerAction.promoteToAdmin(sessionId, studentId, domainId, isAdmin);
    }

    @ApiMethod(name = "subscribeToPublicDomain")
    public UserBean subscribeToPublicDomain(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId)
            throws BadRequestException, UnauthorizedException {

        return SubscribeToDomainServerAction.subscribeToPublicDomain(sessionId, domainId);
    }

    @ApiMethod(name = "subscribeToPrivateDomain")
    public UserBean subscribeToPrivateDomain(
            @Named("sessionId") Long sessionId,
            @Named("invitationCode") String invitationCode)
            throws BadRequestException, UnauthorizedException {

        return SubscribeToDomainServerAction.subscribeToPrivateDomain(sessionId, invitationCode);
    }

    @ApiMethod(name = "createPrivateDomain")
    public DomainBean createPrivateDomain(
            @Named("sessionId") Long sessionId,
            @Named("domainName") String domainName,
            @Named("domainDescription") String domainDescription)
            throws UnauthorizedException, DuplicateDomainNameException {

        return CreatePrivateDomainServerAction.createPrivateDomain(
                sessionId,
                domainName,
                domainDescription);
    }

    @ApiMethod(name = "saveDomain")
    public DomainBean saveDomain(
            @Named("sessionId") Long sessionId,
            @Named("domainId") Long domainId,
            @Named("domainName") String domainName,
            @Named("domainDescription") String domainDescription)
            throws
            DuplicateDomainNameException,
            BadRequestException,
            UnauthorizedException,
            IOException {

        return SaveDomainServerAction.save(sessionId, domainId, domainName, domainDescription);
    }

    @ApiMethod(name = "cleanTestData")
    public void cleanTestData(@Named("passCode") Long passCode) {
        CleanTestDataServerAction.cleanTestData(passCode);
    }
}
