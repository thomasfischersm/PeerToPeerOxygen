package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;
import com.playposse.peertopeeroxygen.backend.util.InvitationCodeGenerator;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Server action that creates new private domains.
 */
public class CreatePrivateDomainServerAction extends ServerAction {

    public static DomainBean createPrivateDomain(
            Long sessionId,
            String domainName,
            String domainDescription)
            throws UnauthorizedException {

        // Prepare data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        Ref<MasterUser> masterUserRef = RefUtil.createMasterUserRef(masterUser);
        String invitationCode = InvitationCodeGenerator.generateCode();

        // Create new private domain.
        Domain domain =
                new Domain(domainName, domainDescription, invitationCode, masterUserRef, false);
        ofy().save().entity(domain).now();

        return new DomainBean(domain);
    }
}