package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A optional base class for server actions that provides useful methods.
 */
public class ServerAction {

    protected static MissionTree findMissionTree(MissionLadder missionLadder, Long missionTreeId) {
        for (Ref<MissionTree> missionTreeRef : missionLadder.getMissionTreeRefs()) {
            if (missionTreeId.equals(missionTreeRef.getKey().getId())) {
                return missionTreeRef.getValue();
            }
        }
        return null;
    }

    protected static OxygenUser loadUserById(Long userId) throws UnauthorizedException {
        OxygenUser oxygenUser = ofy()
                .load()
                .type(OxygenUser.class)
                .id(userId)
                .now();
        if (oxygenUser == null) {
            throw new UnauthorizedException("user id is not found: " + userId);
        }
        return oxygenUser;
    }

    public static OxygenUser loadUserBySessionId(Long sessionId) throws UnauthorizedException {
        List<OxygenUser> oxygenUsers = ofy()
                .load()
                .type(OxygenUser.class)
                .filter("sessionId", sessionId)
                .list();
        if (oxygenUsers.size() != 1) {
            throw new UnauthorizedException("SessionId is not found: " + sessionId
                    + " users found count: " + oxygenUsers.size());
        }
        return oxygenUsers.get(0);
    }

    /**
     * Clears any data that could be a security issue.
     */
    protected static UserBean stripForSafety(UserBean userBean) {
        userBean.setSessionId(null);
        return userBean;
    }

    protected static List<UserBean> stripForSafety(List<UserBean> userBeans) {
        for (UserBean userBean : userBeans) {
            stripForSafety(userBean);
        }

        return userBeans;
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
}
