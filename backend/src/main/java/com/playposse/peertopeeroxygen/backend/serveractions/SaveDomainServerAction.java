package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
import com.playposse.peertopeeroxygen.backend.exceptions.DuplicateDomainNameException;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionDataInvalidationServerAction;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A {@link ServerAction} that saves updates to the domain information.
 */
public class SaveDomainServerAction extends ServerAction {

    public static DomainBean save(
            Long sessionId,
            Long domainId,
            String domainName,
            String domainDescription)
            throws
            UnauthorizedException,
            BadRequestException,
            DuplicateDomainNameException,
            IOException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);
        Domain domain = oxygenUser.getDomainRef().get();

        // Check for duplicate domain name.
        if (!domain.getName().equals(domainName)) {
            checkDuplicateDomainName(domainName);
        }

        // Save domain changes.
        domain.setName(domainName);
        domain.setDescription(domainDescription);
        ofy().save().entity(domain).now();

        // Update all the subscribed students.
        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation(domainId);

        return new DomainBean(domain);
    }
}
