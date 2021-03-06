package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Equivalent of {@link OxygenUser} for transport across the network.
 */
public class UserBean {

    private Long id;
    private boolean isAdmin;
    private String firebaseToken;
    private String fbProfileId;
    private String firstName;
    private String lastName;
    private String name;
    private Long created;
    private Long domainId;
    private List<MissionCompletionBean> missionCompletionBeans = new ArrayList<>();
    private Map<String, Integer> pointsMap = new HashMap<>();
    private List<LevelCompletionBean> levelCompletionBeans = new ArrayList<>();

    public UserBean() {
    }

    public UserBean(OxygenUser oxygenUser) {
        this(oxygenUser.getMasterUserRef().get(), oxygenUser);
    }

    public UserBean(MasterUser masteruser, OxygenUser oxygenUser) {
        id = oxygenUser.getId();
        isAdmin = oxygenUser.isAdmin();
        firebaseToken = masteruser.getFirebaseToken();
        fbProfileId = masteruser.getFbProfileId();
        firstName = masteruser.getFirstName();
        lastName = masteruser.getLastName();
        name = masteruser.getName();
        created = masteruser.getCreated();
        domainId = RefUtil.getDomainId(oxygenUser);

        for (MissionCompletion missionCompletion : oxygenUser.getMissionCompletions().values()) {
            missionCompletionBeans.add(new MissionCompletionBean(missionCompletion));
        }

        for (LevelCompletion levelCompletion : oxygenUser.getLevelCompletions()) {
            levelCompletionBeans.add(new LevelCompletionBean(levelCompletion));
        }

        for (UserPoints point : oxygenUser.getPoints().values()) {
            pointsMap.put(point.getType().name(), point.getCount());
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getFbProfileId() {
        return fbProfileId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public Map<String, Integer> getPointsMap() {
        return pointsMap;
    }

    public Long getCreated() {
        return created;
    }

    public Long getDomainId() {
        return domainId;
    }

    public List<MissionCompletionBean> getMissionCompletionBeans() {
        return missionCompletionBeans;
    }

    public void setMissionCompletionBeans(List<MissionCompletionBean> missionCompletionBeans) {
        this.missionCompletionBeans = missionCompletionBeans;
    }

    public List<LevelCompletionBean> getLevelCompletionBeans() {
        return levelCompletionBeans;
    }

    public void setLevelCompletionBeans(List<LevelCompletionBean> levelCompletionBeans) {
        this.levelCompletionBeans = levelCompletionBeans;
    }
}
