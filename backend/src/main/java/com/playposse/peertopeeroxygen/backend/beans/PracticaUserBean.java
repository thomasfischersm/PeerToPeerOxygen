package com.playposse.peertopeeroxygen.backend.beans;

import com.google.appengine.repackaged.com.google.api.client.json.JsonString;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.List;
import java.util.Map;

/**
 * A transport bean that is specialized for transporting compacted user information to other
 * practica participants. In particular, the completed missions are compressed into a string of
 * mission ids.
 */

public class PracticaUserBean {

    @JsonString private Long id;
    private boolean isAdmin;
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;
    private String studiedMissions;
    private String completedLevels;

    public PracticaUserBean() {
    }

    public PracticaUserBean(OxygenUser user) {
        MasterUser masterUser = user.getMasterUserRef().get();

        id = user.getId();
        isAdmin = user.isAdmin();
        firstName = masterUser.getFirstName();
        lastName = masterUser.getLastName();
        name = masterUser.getName();
        profilePictureUrl = masterUser.getProfilePictureUrl();
        studiedMissions = buildStudiedMissionString(user.getMissionCompletions());
        completedLevels = buildCompletedLevelsString(user.getLevelCompletions());
    }

    private String buildStudiedMissionString(Map<Long, MissionCompletion> missionCompletions) {
        StringBuilder sb = new StringBuilder();
        if (missionCompletions != null) {
            for (MissionCompletion missionCompletion : missionCompletions.values()) {
                if (missionCompletion.isStudyComplete()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(missionCompletion.getMission().getKey().getId());
                }
            }
        }
        return sb.toString();
    }

    private String buildCompletedLevelsString(List<LevelCompletion> levelCompletions) {
        StringBuilder sb = new StringBuilder();
        if (levelCompletions != null) {
            for (LevelCompletion levelCompletion : levelCompletions) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(levelCompletion.getMissionTreeRef().getKey().getId());
            }
        }
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getStudiedMissions() {
        return studiedMissions;
    }

    public String getCompletedLevels() {
        return completedLevels;
    }
}
