package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;

/**
 * A bean for the Endpoint to transmit master user information.
 */
public class MasterUserBean {

    private Long id;
    private Long sessionId;
    private String fbProfileId;
    private String firebaseToken;
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;
    private String email;

    public MasterUserBean() {
    }

    public MasterUserBean(MasterUser masterUser) {
        id = masterUser.getId();
        sessionId = masterUser.getSessionId();
        fbProfileId = masterUser.getFbProfileId();
        firebaseToken = masterUser.getFirebaseToken();
        firstName = masterUser.getFirstName();
        lastName = masterUser.getLastName();
        name = masterUser.getName();
        profilePictureUrl = masterUser.getProfilePictureUrl();
        email = masterUser.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getFbProfileId() {
        return fbProfileId;
    }

    public void setFbProfileId(String fbProfileId) {
        this.fbProfileId = fbProfileId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
