package com.playposse.peertopeeroxygen.backend.beans;

import com.google.api.client.json.JsonString;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.util.ArrayList;
import java.util.List;

import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.createDomainRef;

/**
 * Equivalent of {@link Practica} for transport across the network.
 */
public class PracticaBean {

    private Long id;
    private String name;
    private String greeting;
    private Long start;
    private Long end;
    private String address;
    private String gpsLocation;
    private PracticaUserBean hostUserBean;
    private List<PracticaUserBean> attendeeUserBeans = new ArrayList<>();
    private Long created;
    private String timezone;
    private Long domainId;

    public PracticaBean() {
    }

    public PracticaBean(Practica practica) {
        this.id = practica.getId();
        this.name = practica.getName();
        this.greeting = practica.getGreeting();
        this.start = practica.getStart();
        this.end = practica.getEnd();
        this.address = practica.getAddress();
        this.gpsLocation = practica.getGpsLocation();
        this.created = practica.getCreated();
        this.timezone = practica.getTimezone();
        this.hostUserBean = new PracticaUserBean(practica.getHostUser().get());
        this.domainId = practica.getDomainRef().getKey().getId();

        for (Ref<OxygenUser> attendee : practica.getAttendeeUsers()) {
            attendeeUserBeans.add(new PracticaUserBean(attendee.get()));
        }
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

    public PracticaUserBean getHostUserBean() {
        return hostUserBean;
    }

    public void setHostUserBean(PracticaUserBean hostUserBean) {
        this.hostUserBean = hostUserBean;
    }

    public List<PracticaUserBean> getAttendeeUserBeans() {
        return attendeeUserBeans;
    }

    public void setAttendeeUserBeans(List<PracticaUserBean> attendeeUserBeans) {
        this.attendeeUserBeans = attendeeUserBeans;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Practica toEntity() {
        List<Ref<OxygenUser>> attendeeEntities = new ArrayList<>(attendeeUserBeans.size());
        for (PracticaUserBean practicaUserBean : attendeeUserBeans) {
            Ref<OxygenUser> userRef =
                    Ref.create(Key.create(OxygenUser.class, practicaUserBean.getId()));
            attendeeEntities.add(userRef);
        }

        return new Practica(
                name,
                greeting,
                start,
                end,
                address,
                gpsLocation,
                Ref.create(Key.create(OxygenUser.class, hostUserBean.getId())),
                attendeeEntities,
                timezone,
                createDomainRef(domainId));
    }
}
