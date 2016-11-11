package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that returns meta information about all public domains.
 */
public class GetPublicDomainsServerAction extends ServerAction {

    public static List<DomainBean> getPublicDomains(Long sessionId) throws UnauthorizedException {

        // Limit access to only users. No third parties should scan this (easily).
        loadMasterUserBySessionId(sessionId);

        List<Domain> domains = ofy().load().type(Domain.class).filter("isPublic =", true).list();

        List<DomainBean> domainBeans = new ArrayList<>(domains.size());
        for (Domain domain : domains) {
            domainBeans.add(new DomainBean(domain));
        }
        return domainBeans;
    }
}
