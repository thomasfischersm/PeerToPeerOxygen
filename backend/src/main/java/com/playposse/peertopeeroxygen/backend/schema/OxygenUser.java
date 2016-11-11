package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.Stringify;
import com.playposse.peertopeeroxygen.backend.PeerToPeerOxygenEndPoint;
import com.playposse.peertopeeroxygen.backend.schema.util.LongStringifier;
import com.playposse.peertopeeroxygen.backend.schema.util.MigrationConstants;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Objectify entity that represents a user.
 */
@Entity
@Cache
public class OxygenUser {

    private static final Logger log = Logger.getLogger(OxygenUser.class.getName());

    @Id private Long id;
    private boolean isAdmin = false;
    @Deprecated @Index private String fbProfileId;
    @Deprecated @Index private Long sessionId = null;
    @Deprecated private String firebaseToken;
    private Long lastLogin = System.currentTimeMillis();
    private Long created = System.currentTimeMillis();
    @Deprecated private String firstName;
    @Deprecated private String lastName;
    @Deprecated private String name;
    @Deprecated private String profilePictureUrl;
    @Deprecated private String email;
    @Load private Map<UserPoints.PointType, UserPoints> points = new HashMap<>();
    @Load private List<LevelCompletion> levelCompletions = new ArrayList<>();
    @Load @Nullable private Ref<Practica> activePracticaRef;
    @Index private Ref<Domain> domainRef;
    private Ref<MasterUser> masterUserRef;

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
    public OxygenUser(MasterUser masterUser, boolean isAdmin, Ref<Domain> domainRef) {
        this.isAdmin = isAdmin;
        this.domainRef = domainRef;
        this.masterUserRef = RefUtil.createMasterUserRef(masterUser);
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

    @Deprecated
    public String getFbProfileId() {
        return fbProfileId;
    }

    @Deprecated
    public void setFbProfileId(String fbProfileId) {
        this.fbProfileId = fbProfileId;
    }

    @Deprecated
    public String getFirstName() {
        return firstName;
    }

    @Deprecated
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

    @Deprecated
    public String getLastName() {
        return lastName;
    }

    @Deprecated
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    public Map<UserPoints.PointType, UserPoints> getPoints() {
        return points;
    }

    public void setPoints(Map<UserPoints.PointType, UserPoints> points) {
        this.points = points;
    }

    @Deprecated
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @Deprecated
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @Deprecated
    public String getFirebaseToken() {
        return firebaseToken;
    }

    @Deprecated
    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Deprecated
    public String getEmail() {
        return email;
    }

    @Deprecated
    public void setEmail(String email) {
        this.email = email;
    }

    public void addPoints(UserPoints.PointType pointType, int pointCount) {
        if (points.containsKey(pointType)) {
            UserPoints userPoints = points.get(pointType);
            userPoints.setCount(userPoints.getCount() + pointCount);
        } else {
            points.put(pointType, new UserPoints(pointType, pointCount));
        }
    }

    public List<LevelCompletion> getLevelCompletions() {
        return levelCompletions;
    }

    public void setActivePracticaRef(@Nullable Ref<Practica> activePracticaRef) {
        this.activePracticaRef = activePracticaRef;
    }

    public Ref<Practica> getActivePracticaRef() {
        return activePracticaRef;
    }

    public Ref<Domain> getDomainRef() {
        return domainRef;
    }

    public void setDomainRef(Ref<Domain> domainRef) {
        this.domainRef = domainRef;
    }

    public Ref<MasterUser> getMasterUserRef() {
        return masterUserRef;
    }

    @OnLoad
    public void migrateToMultiDomainSupport() {
        if ((domainRef == null) && (MigrationConstants.DEFAULT_DOMAIN != null)) {
            log.info("OxygenUser.OnLoad setting default domain.");
            domainRef = RefUtil.createDomainRef(MigrationConstants.DEFAULT_DOMAIN);
            ofy().save().entity(this).now();
        }
    }

    @OnLoad
    public void migrateToMasterUser() {
        if (masterUserRef != null) {
            return;
        }

        log.info("OxygenUser.OnLoad creating missing MasterUser record.");
        ArrayList<Ref<OxygenUser>> oxygenUserRefs = new ArrayList<>();
        oxygenUserRefs.add(RefUtil.createOxygenUserRef(this));
        MasterUser masterUser = new MasterUser(
                new ObjectifyFactory().allocateId(MasterUser.class).getId(),
                fbProfileId,
                sessionId,
                firebaseToken,
                firstName,
                lastName,
                name,
                profilePictureUrl,
                email,
                oxygenUserRefs);
        ofy().save().entity(masterUser).now();

        masterUserRef = RefUtil.createMasterUserRef(masterUser);
        ofy().save().entity(this).now();
    }
}
