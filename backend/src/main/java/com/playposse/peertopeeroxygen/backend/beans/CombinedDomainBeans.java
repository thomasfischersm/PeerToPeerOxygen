package com.playposse.peertopeeroxygen.backend.beans;

import java.util.List;

/**
 * A bean to transport data from the cloud to the client, which is simply a response wrapper to
 * combine subscribed and public beans.
 */
public class CombinedDomainBeans {

    private final List<DomainBean> subscribedBeans;
    private final List<DomainBean> publicBeans;

    public CombinedDomainBeans(List<DomainBean> publicBeans, List<DomainBean> subscribedBeans) {
        this.publicBeans = publicBeans;
        this.subscribedBeans = subscribedBeans;
    }

    public List<DomainBean> getSubscribedBeans() {
        return subscribedBeans;
    }

    public List<DomainBean> getPublicBeans() {
        return publicBeans;
    }
}
