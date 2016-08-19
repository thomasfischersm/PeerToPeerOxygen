package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Stringify;

import java.util.HashMap;
import java.util.Map;

/**
 * Objectify entity that represents a user.
 */
@Entity
public class OxygenUser {

    @Id private Long id;
    private boolean isAdmin = false;
    @Index private String fbProfileId;
    @Index private Long sessionId = null;
    private String firebaseToken;
    private Long lastLogin = System.currentTimeMillis();
    private Long created = System.currentTimeMillis();
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;
    private Map<UserPoints.PointType, UserPoints> points = new HashMap<>();

    @Stringify(LongStringifier.class)
    private Map<Long, MissionCompletion> missionCompletions = new HashMap<>();

    /**
     * Default constructor for Objectify.
     */
    public OxygenUser() {
    }

    /**
     * Constructor for creating new users.
     */
    public OxygenUser(
            Long sessionId,
            String fbProfileId,
            String firebaseToken,
            String name,
            String firstName,
            String lastName,
            String profilePictureUrl,
            Long lastLogin,
            boolean isAdmin) {

        this.firstName = firstName;
        this.lastLogin = lastLogin;
        this.lastName = lastName;
        this.profilePictureUrl = profilePictureUrl;
        this.sessionId = sessionId;
        this.name = name;
        this.fbProfileId = fbProfileId;
        this.firebaseToken = firebaseToken;
        this.isAdmin = isAdmin;
    }

    public Map<Long, MissionCompletion> getMissionCompletions() {
        return missionCompletions;
    }

    public void setMissionCompletions(Map<Long, MissionCompletion> missionCompletions) {
        this.missionCompletions = missionCompletions;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getFbProfileId() {
        return fbProfileId;
    }

    public void setFbProfileId(String fbProfileId) {
        this.fbProfileId = fbProfileId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<UserPoints.PointType, UserPoints> getPoints() {
        return points;
    }

    public void setPoints(Map<UserPoints.PointType, UserPoints> points) {
        this.points = points;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
