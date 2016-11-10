package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.playposse.peertopeeroxygen.backend.schema.util.MigrationConstants;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An Objectify instance that represents a practica event.
 */
@Entity
@Cache
public class Practica {

    @Id private Long id;
    private String name;
    private String greeting;
    @Index private Long start;
    @Index private Long end;
    private String address;
    private String gpsLocation;
    @Index @Load private Ref<OxygenUser> hostUser;
    @Load private List<Ref<OxygenUser>> attendeeUsers = new ArrayList<>();
    private Long created = System.currentTimeMillis();
    private String timezone;
    @Index private Ref<Domain> domainRef;

    public Practica() {
    }

    public Practica(
            String name,
            String greeting,
            Long start,
            Long end,
            String address,
            String gpsLocation,
            Ref<OxygenUser> hostUser,
            List<Ref<OxygenUser>> attendeeUsers,
            String timezone,
            Ref<Domain> domainRef) {

        this.name = name;
        this.greeting = greeting;
        this.start = start;
        this.end = end;
        this.address = address;
        this.gpsLocation = gpsLocation;
        this.hostUser = hostUser;
        this.attendeeUsers = attendeeUsers;
        this.timezone = timezone;
        this.domainRef = domainRef;
    }

    public Long getId() {
        return id;
    }

    public Long getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public Ref<OxygenUser> getHostUser() {
        return hostUser;
    }

    public void setHostUser(Ref<OxygenUser> hostUser) {
        this.hostUser = hostUser;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<Ref<OxygenUser>> getAttendeeUsers() {
        return attendeeUsers;
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
