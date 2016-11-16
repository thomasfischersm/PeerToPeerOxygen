package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A optional base class for server actions that provides useful methods.
 */
public class ServerAction {

    protected static void protectByAdminCheck(
            MasterUser masterUser,
            OxygenUser oxygenUser,
            Long domainId)
            throws UnauthorizedException {

        if (!oxygenUser.isAdmin()) {
            throw new UnauthorizedException(
                    "The user is NOT an admin: " + masterUser.getId());
        } else if (!domainId.equals(oxygenUser.getDomainRef().getKey().getId())) {
            throw new UnauthorizedException(
                    "User " + oxygenUser.getId() + " does not belong to domain " + domainId
                            + " but " + oxygenUser.getDomainRef().getKey().getId());
        }
    }

    protected static void verifyUserByDomain(OxygenUser oxygenUser, Long expectedDomainId)
            throws BadRequestException {

        Long actualDomainId = oxygenUser.getDomainRef().getKey().getId();
        if (!expectedDomainId.equals(actualDomainId)) {
            throw new BadRequestException("The domain user " + oxygenUser.getId()
                    + " doesn't belong to domain " + expectedDomainId + " but " + actualDomainId);
        }
    }

    protected  static void verifyMissionLadderByDomain(
            MissionLadder missionLadder,
            Long expectedDomainId)
            throws BadRequestException {

        Long actualDomainId = missionLadder.getDomainRef().getKey().getId();
        if (!expectedDomainId.equals(actualDomainId)) {
            throw new BadRequestException("The mission ladder " + missionLadder.getId()
                    + " doesn't belong to domain " + expectedDomainId + " but " + actualDomainId);
        }
    }

    protected  static void verifyMissionTreeByDomain(
            MissionTree missionTree,
            Long expectedDomainId)
            throws BadRequestException {

        Long actualDomainId = missionTree.getDomainRef().getKey().getId();
        if (!expectedDomainId.equals(actualDomainId)) {
            throw new BadRequestException("The mission tree " + missionTree.getId()
                    + " doesn't belong to domain " + expectedDomainId + " but " + actualDomainId);
        }
    }

    protected static void verifyMissionByDomain(
            Mission mission,
            Long expectedDomainId)
            throws BadRequestException {

        Long actualDomainId = mission.getDomainRef().getKey().getId();
        if (!expectedDomainId.equals(actualDomainId)) {
            throw new BadRequestException("The mission " + mission.getId()
                    + " doesn't belong to domain " + expectedDomainId + " but " + actualDomainId);
        }
    }

    protected static void verifyPracticaByDomain(Practica practica, Long expectedDomainId)
            throws BadRequestException {

        Long actualDomainId = practica.getDomainRef().getKey().getId();
        if (!expectedDomainId.equals(actualDomainId)) {
            throw new BadRequestException("The practica " + practica.getId()
                    + " doesn't belong to domain " + expectedDomainId + " but " + actualDomainId);
        }
    }

    protected static void verifyPracticaByDomain(PracticaBean practicaBean, Long domainId)
            throws UnsupportedEncodingException, BadRequestException {

        if (!domainId.equals(practicaBean.getDomainId())) {
            String practicaName = URLEncoder.encode(practicaBean.getName(), "UTF-8");
            throw new BadRequestException("Tried to save practica '" + practicaName
                    + "' to domain " + domainId + " but it was domain "
                    + practicaBean.getDomainId());
        }
    }

    protected static MissionTree findMissionTree(MissionLadder missionLadder, Long missionTreeId) {
        for (Ref<MissionTree> missionTreeRef : missionLadder.getMissionTreeRefs()) {
            if (missionTreeId.equals(missionTreeRef.getKey().getId())) {
                return missionTreeRef.get();
            }
        }
        return null;
    }

    protected static OxygenUser loadOxygenUserById(Long userId, Long domainId)
            throws UnauthorizedException {

        OxygenUser oxygenUser = ofy()
                .load()
                .type(OxygenUser.class)
                .id(userId)
                .now();
        if (oxygenUser == null) {
            throw new UnauthorizedException("user id is not found: " + userId);
        }
        if (!domainId.equals(oxygenUser.getDomainRef().getKey().getId())) {
            throw new UnauthorizedException(
                    "User (id: " + userId + " doesn't belong to domain (id: " + domainId + ").");
        }
        return oxygenUser;
    }

    protected static MasterUser loadMasterUserBySessionId(Long sessionId)
            throws UnauthorizedException {
        return loadMasterUserBySessionId(sessionId, true);
    }

    private static MasterUser loadMasterUserBySessionId(Long sessionId, boolean shouldRetry)
            throws UnauthorizedException {

        List<MasterUser> masterUsers = ofy()
                .load()
                .type(MasterUser.class)
                .filter("sessionId", sessionId)
                .list();

        if (masterUsers.size() != 1) {
            if (shouldRetry) {
                // This query should trigger OxygenUsers to be migrated to MasterUsers.
                ofy()
                        .load()
                        .type(OxygenUser.class)
                        .filter("sessionId", sessionId)
                        .list()
                        .size();
                return loadMasterUserBySessionId(sessionId, false);
            } else {
                throw new UnauthorizedException("SessionId is not found: " + sessionId
                        + " users found count: " + masterUsers.size());
            }
        }

        return masterUsers.get(0);
    }

    protected static OxygenUser findOxygenUserByDomain(MasterUser masterUser, Long domainId)
            throws BadRequestException {

        OxygenUser oxygenUser = masterUser.getOxygenUser(domainId);
        if (oxygenUser != null) {
            return oxygenUser;
        } else {
            throw new BadRequestException("Master user " + masterUser.getId()
                    + " doesn't have a domain user for domain " + domainId);
        }
    }

    protected static LevelCompletion getLevelCompletion(OxygenUser user, Long missionTreeId) {
        if (user.getLevelCompletions() != null) {
            for (LevelCompletion levelCompletion : user.getLevelCompletions()) {
                if (missionTreeId.equals(levelCompletion.getMissionTreeRef().getKey().getId())) {
                    return levelCompletion;
                }
            }
        }
        return null;
    }

    protected static MissionStats getMissionStats(Long missionId, Long expectedDomainId)
            throws BadRequestException {

        Key<Mission> missionKey = Key.create(Mission.class, missionId);
        List<MissionStats> missionStatsList =
                ofy().load().type(MissionStats.class).filter("missionRef", missionKey).list();

        if ((missionStatsList == null) || (missionStatsList.size() == 0)) {
            Ref<Domain> domainRef = Ref.create(Key.create(Domain.class, expectedDomainId));
            return new MissionStats(0, Ref.create(missionKey), 0, 0, domainRef);
        } else {
            MissionStats missionStats = missionStatsList.get(0);
            Long actualDomainId = missionStats.getDomainRef().getKey().getId();
            if (expectedDomainId.equals(actualDomainId)) {
                return missionStats;
            } else {
                throw new BadRequestException("Mission Stat was expected to belong to domain "
                        + expectedDomainId + " but actually belonged to domain " + actualDomainId);
            }
        }
    }

}
