package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
import com.playposse.peertopeeroxygen.backend.exceptions.DuplicateDomainNameException;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;
import com.playposse.peertopeeroxygen.backend.util.InvitationCodeGenerator;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Server action that creates new private domains.
 */
public class CreatePrivateDomainServerAction extends ServerAction {

    public static DomainBean createPrivateDomain(
            Long sessionId,
            String domainName,
            String domainDescription)
            throws UnauthorizedException, DuplicateDomainNameException {

        // Prepare data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        Ref<MasterUser> masterUserRef = RefUtil.createMasterUserRef(masterUser);
        String invitationCode = InvitationCodeGenerator.generateCode();

        // Check for existing name.
        checkDuplicateDomainName(domainName);

        // Create new private domain.
        Domain domain =
                new Domain(domainName, domainDescription, invitationCode, masterUserRef, false);
        ofy().save().entity(domain).now();
        Ref<Domain> domainRef = RefUtil.createDomainRef(domain);

        // Make the caller an admin.
        OxygenUser domainUser = new OxygenUser(masterUser, true, domainRef);
        ofy().save().entity(domainUser).now();

        Ref<OxygenUser> domainuUserRef = RefUtil.createOxygenUserRef(domainUser);
        masterUser.getDomainUserRefs().add(domainuUserRef);
        ofy().save().entity(masterUser).now();

        return new DomainBean(domain);
    }
}
