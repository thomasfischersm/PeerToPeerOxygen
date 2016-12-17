package com.playposse.peertopeeroxygen.android.firebase.actions.data;

import com.playposse.peertopeeroxygen.android.firebase.actions.PracticaUpdateClientAction;

/**
 * Stop gap measure until we can figure out the parsing problem.
 */
public class TempPracticaBean {
    private Long id;
    private String name;
    private String greeting;
    private Long start;
    private Long end;
    private String address;
    private String gpsLocation;
    private TempPracticaUserBean hostUserBean;
//        private List<PracticaUserBean> attendeeUserBeans = new ArrayList<>();
    private Long created;
    private String timezone;
    private Long domainId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public TempPracticaUserBean getHostUserBean() {
        return hostUserBean;
    }

    public void setHostUserBean(TempPracticaUserBean hostUserBean) {
        this.hostUserBean = hostUserBean;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
