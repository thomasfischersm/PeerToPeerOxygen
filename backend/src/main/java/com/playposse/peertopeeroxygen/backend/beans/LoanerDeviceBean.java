package com.playposse.peertopeeroxygen.backend.beans;

import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;

/**
 * An end point bean that describes a loaner device.
 */
public class LoanerDeviceBean {

    private Long id;
    private String friendlyName;
    private Long created;
    private Long lastLogin;
    private UserBean lastUserBean;

    public LoanerDeviceBean() {
    }

    public LoanerDeviceBean(LoanerDevice loanerDevice) {
        this.id = loanerDevice.getId();
        this.friendlyName = loanerDevice.getFriendlyName();
        this.created = loanerDevice.getCreated();
        this.lastLogin = loanerDevice.getLastLogin();
        this.lastUserBean = new UserBean(loanerDevice.getLastUserRef().get());
    }

    public Long getCreated() {
        return created;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Long getId() {
        return id;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public UserBean getLastUserBean() {
        return lastUserBean;
    }
}
