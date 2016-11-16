package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * An Objectify entity that represents a user. There is one {@link MasterUser} that is linked to
 * one {@link OxygenUser} for each domain.
 */
@Entity
@Cache
public class MasterUser {

    @Id private Long id;
    @Index private String fbProfileId;
    @Index private Long sessionId = null;
    private String firebaseToken;
    private Long lastLogin = System.currentTimeMillis();
    private Long created = System.currentTimeMillis();
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;
    private String email;
    private List<Ref<OxygenUser>> domainUserRefs = new ArrayList<>();

    public MasterUser() {
    }

    public MasterUser(
            Long id,
            String fbProfileId,
            Long sessionId,
            String firebaseToken,
            String firstName,
            String lastName,
            String name,
            String profilePictureUrl,
            String email,
            List<Ref<OxygenUser>> domainUserRefs) {

        this.id = id;
        this.fbProfileId = fbProfileId;
        this.sessionId = sessionId;
        this.firebaseToken = firebaseToken;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.domainUserRefs = domainUserRefs;
    }

    public Long getId() {
        return id;
    }

    public Long getCreated() {
        return created;
    }

    public String getFbProfileId() {
        return fbProfileId;
    }

    public void setFbProfileId(String fbProfileId) {
        this.fbProfileId = fbProfileId;
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

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
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

    public List<Ref<OxygenUser>> getDomainUserRefs() {
        return domainUserRefs;
    }

    public void setDomainUserRefs(List<Ref<OxygenUser>> domainUserRefs) {
        this.domainUserRefs = domainUserRefs;
    }

    public OxygenUser getOxygenUser(Long domainId) {
        if (getDomainUserRefs() != null) {
            for (Ref<OxygenUser> oxygenUserRef : getDomainUserRefs()) {
                OxygenUser oxygenUser = oxygenUserRef.get();

                if (oxygenUser == null) {
                    // Skip the reference to the deleted OxygenUser.
                    continue;
                }

                long actualDomainId = oxygenUser.getDomainRef().getKey().getId();
                if (equals(actualDomainId)) {
                    return oxygenUser;
                }
            }
        }
        return null;
    }
}
