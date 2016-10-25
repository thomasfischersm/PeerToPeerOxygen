package com.playposse.peertopeeroxygen.android.firebase.actions.data;

/**
 * Stop gap measure until decoding JSON with the generated
 * {@link com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean} is
 * figured out.
 */
public class TempPracticaUserBean {

    private Long id;
    private boolean isAdmin;
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;
    private String studiedMissions;
    private String completedLevels;

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

    public String getStudiedMissions() {
        return studiedMissions;
    }

    public void setStudiedMissions(String studiedMissions) {
        this.studiedMissions = studiedMissions;
    }

    public String getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(String completedLevels) {
        this.completedLevels = completedLevels;
    }
}
