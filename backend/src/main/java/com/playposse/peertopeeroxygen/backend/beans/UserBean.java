package com.playposse.peertopeeroxygen.backend.beans;

import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

/**
 * Equivalent of {@link OxygenUser} for transport across the network.
 */
public class UserBean {

    private Long id;
    private boolean isAdmin;
    private String fbProfileId;
    private Long sessionId;
    private String firebaseToken;
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;

    public UserBean() {
    }

    public UserBean(OxygenUser oxygenUser) {
        id = oxygenUser.getId();
        isAdmin = oxygenUser.isAdmin();
        fbProfileId = oxygenUser.getFbProfileId();
        sessionId = oxygenUser.getSessionId();
        firebaseToken = oxygenUser.getFirebaseToken();
        firstName = oxygenUser.getFirstName();
        lastName = oxygenUser.getLastName();
        name = oxygenUser.getLastName();
        profilePictureUrl = oxygenUser.getProfilePictureUrl();
    }

    public String getFbProfileId() {
        return fbProfileId;
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

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
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
