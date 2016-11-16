package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Server action that subscribes a user to a private domain.
 */
public class SubscribeToDomainServerAction extends ServerAction {

    public static UserBean subscribeToPublicDomain(Long sessionId, Long domainId)
            throws BadRequestException, UnauthorizedException {

        return subscribeToDomain(sessionId, domainId);
    }

    public static UserBean subscribeToPrivateDomain(Long sessionId, String invitationCode)
            throws BadRequestException, UnauthorizedException {

        List<Domain> domains = ofy()
                .load()
                .type(Domain.class)
                .filter("invitationCode =", invitationCode)
                .list();

        if (domains.size() != 1) {
            throw new BadRequestException("The invitation code wasn't found: " + invitationCode);
        }

        return subscribeToDomain(sessionId, domains.get(0).getId());
    }

    private static UserBean subscribeToDomain(Long sessionId, Long domainId)
            throws UnauthorizedException {
        // Load data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);

        // Check for duplicate
        OxygenUser existingOxygenUser = masterUser.getOxygenUser(domainId);
        if (existingOxygenUser != null) {
            return new UserBean(masterUser, existingOxygenUser);
        }

        // Create OxygenUser.
        Ref<Domain> domainRef = RefUtil.createDomainRef(domainId);
        OxygenUser oxygenUser = new OxygenUser(masterUser, false, domainRef);
        ofy().save().entity(oxygenUser).now();

        // Update MasterUser.
        if (masterUser.getDomainUserRefs() == null) {
            masterUser.setDomainUserRefs(new ArrayList<Ref<OxygenUser>>());
        }
        masterUser.getDomainUserRefs().add(RefUtil.createOxygenUserRef(oxygenUser));
        ofy().save().entity(masterUser).now();

        return new UserBean(masterUser, oxygenUser);
    }
}
