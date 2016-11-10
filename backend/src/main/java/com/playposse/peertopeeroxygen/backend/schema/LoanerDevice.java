package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.playposse.peertopeeroxygen.backend.schema.util.MigrationConstants;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An Objectify entity that describes a loaner device. A loaner device is a device that's owned
 * by the studio and shared.
 */
@Entity
@Cache
public class LoanerDevice {

    @Id private Long id;
    private String friendlyName;
    private Long created = System.currentTimeMillis();
    private Long lastLogin;
    @Load private Ref<OxygenUser> lastUserRef;
    @Index private Ref<Domain> domainRef;

    public LoanerDevice() {
    }

    public LoanerDevice(
            String friendlyName,
            long lastLogin,
            Ref<OxygenUser> lastUserRef,
            Ref<Domain> domainRef) {

        this.friendlyName = friendlyName;
        this.lastLogin = lastLogin;
        this.lastUserRef = lastUserRef;
        this.domainRef = domainRef;
    }

    public void changeUser(OxygenUser user) {
        lastUserRef = Ref.create(Key.create(OxygenUser.class, user.getId()));
        lastLogin = System.currentTimeMillis();
    }

    public long getCreated() {
        return created;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Long getId() {
        return id;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public Ref<OxygenUser> getLastUserRef() {
        return lastUserRef;
    }

    public Ref<Domain> getDomainRef() {
        return domainRef;
    }

    public void setDomainRef(Ref<Domain> domainRef) {
        this.domainRef = domainRef;
    }

    @OnLoad
    public void migrateToMultiDomainSupport() {
        if ((domainRef == null) && (MigrationConstants.DEFAULT_DOMAIN != null)) {
            domainRef = RefUtil.createDomainRef(MigrationConstants.DEFAULT_DOMAIN);
            ofy().save().entity(this).now();
        }
    }
}
