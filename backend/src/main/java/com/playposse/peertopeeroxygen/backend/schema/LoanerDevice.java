package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

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

    public LoanerDevice() {
    }

    public LoanerDevice(String friendlyName, long lastLogin, Ref<OxygenUser> lastUserRef) {
        this.friendlyName = friendlyName;
        this.lastLogin = lastLogin;
        this.lastUserRef = lastUserRef;
    }

    public void changeUser(OxygenUser user) {
        Ref<OxygenUser> userRef = Ref.create(Key.create(OxygenUser.class, user.getId()));
        lastUserRef = userRef;
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
}
