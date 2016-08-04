package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
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
    // FB profile information
    private Long sessionId = null;
    private Long lastLogin = System.currentTimeMillis();
    private Long created = System.currentTimeMillis();
    private String firstName;
    private String lastName;
    private String profilePhotoUrl;
    private Map<UserPoints.PointType, UserPoints> points = new HashMap<>();

    @Stringify(LongStringifier.class)
    private Map<Long, MissionCompleted> completedMissions = new HashMap<>();

    /**
     * Default constructor for Objectify.
     */
    public OxygenUser() {
    }

    /**
     * Constructor for creating new users.
     */
    public OxygenUser(
            String firstName,
            Long lastLogin,
            String lastName,
            String profilePhotoUrl,
            Long sessionId) {

        this.firstName = firstName;
        this.lastLogin = lastLogin;
        this.lastName = lastName;
        this.profilePhotoUrl = profilePhotoUrl;
        this.sessionId = sessionId;
    }

    public void updateSessionInfo(Long sessionId) {
        this.sessionId = sessionId;
        this.lastLogin = System.currentTimeMillis();
    }

    public Long getCreated() {
        return created;
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

    public Long getLastLogin() {
        return lastLogin;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Map<UserPoints.PointType, UserPoints> getPoints() {
        return points;
    }
}
