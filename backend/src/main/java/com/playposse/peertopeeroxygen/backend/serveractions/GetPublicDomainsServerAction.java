package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.CombinedDomainBeans;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that returns meta information about all public domains.
 */
public class GetPublicDomainsServerAction extends ServerAction {

    public static CombinedDomainBeans getPublicDomains(Long sessionId)
            throws UnauthorizedException {

        // Limit access to only users. No third parties should scan this (easily).
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);

        // Get subscribed domains.
        List<DomainBean> subscribedDomainBeans =
                new ArrayList<>(masterUser.getDomainUserRefs().size());
        for (Ref<OxygenUser> oxygenUserRef : masterUser.getDomainUserRefs()) {
            if ((oxygenUserRef == null)
                    || (oxygenUserRef.get() == null)
                    || (oxygenUserRef.get().getDomainRef() == null)) {
                // Ignore data with deleted references.
                continue;
            }

            Domain domain = oxygenUserRef.get().getDomainRef().get();
            subscribedDomainBeans.add(new DomainBean(domain));
        }

        // Get public domains.
        List<Domain> publicDomains =
                ofy().load().type(Domain.class).filter("isPublic =", true).list();
        List<DomainBean> publicDomainBeans = new ArrayList<>(publicDomains.size());
        for (Domain domain : publicDomains) {
            publicDomainBeans.add(new DomainBean(domain));
        }

        return new CombinedDomainBeans(publicDomainBeans, subscribedDomainBeans);
    }
}
