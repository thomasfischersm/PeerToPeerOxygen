package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that promotes a student to an admin for a particular domain.
 */
public class PromoteToAdminServerAction extends ServerAction {

    public static void promoteToAdmin(
            Long sessionId,
            Long studentId,
            Long domainId,
            boolean isAdmin)
            throws UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Find the student
        OxygenUser studentOxygenUser = loadOxygenUserById(studentId, domainId);

        // Ensure that the domain owner isn't demoted
        if (!isAdmin) {
            Domain domain = studentOxygenUser.getDomainRef().get();
            if (domain == null) {
                throw new BadRequestException("The student's (" + studentOxygenUser.getId()
                        + ") domain (" + studentOxygenUser.getDomainRef().getKey().getId()
                        + ") doesn't exist.");
            }

            MasterUser domainOwner = domain.getOwnerRef().get();
            if (domainOwner == null) {
                throw new BadRequestException("The domain is missing an owner: " + domain.getId());
            }

            Long domainOwnerId = domainOwner.getId();
            Long studentMasterUserId = studentOxygenUser.getMasterUserRef().get().getId();
            if (domainOwnerId.equals(studentMasterUserId)) {
                throw new BadRequestException("Cannot demote admin " + studentMasterUserId
                        + " because the admin owns the domain: " + domainId);
            }
        }

        // Promote the student.
        studentOxygenUser.setAdmin(isAdmin);
        ofy().save().entity(studentOxygenUser);
    }
}
