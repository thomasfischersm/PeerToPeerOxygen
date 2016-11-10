package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.playposse.peertopeeroxygen.backend.schema.util.MigrationConstants;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An Objectify entity that stores missionRef feedback from the userRef;
 */
@Entity
public class MissionFeedback {

    @Id private Long id;
    @Load private Ref<OxygenUser> userRef;
    @Index @Load private Ref<Mission> missionRef;
    private int rating;
    private Long date = System.currentTimeMillis();
    @Nullable private String comment;
    @Index private Ref<Domain> domainRef;

    public MissionFeedback() {
    }

    public MissionFeedback(
            Ref<Mission> missionRef,
            Ref<OxygenUser> userRef,
            int rating,
            @Nullable String comment,
            Ref<Domain> domainRef) {

        this.missionRef = missionRef;
        this.userRef = userRef;
        this.rating = rating;
        this.comment = comment;
        this.domainRef = domainRef;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public Long getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Ref<Mission> getMissionRef() {
        return missionRef;
    }

    public int getRating() {
        return rating;
    }

    public Ref<OxygenUser> getUserRef() {
        return userRef;
    }

    public Ref<Domain> getDomainRef() {
        return domainRef;
    }

    @OnLoad
    public void migrateToMultiDomainSupport() {
        if ((domainRef == null) && (MigrationConstants.DEFAULT_DOMAIN != null)) {
            domainRef = RefUtil.createDomainRef(MigrationConstants.DEFAULT_DOMAIN);
            ofy().save().entity(this).now();
        }
    }
}
