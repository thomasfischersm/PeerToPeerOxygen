package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that promotes a student to an admin for a particular domain.
 */
public class PromoteToAdminServerAction extends ServerAction {

    public static void promoteToAdmin(Long sessionId, Long studentId, Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Find the student
        OxygenUser studentOxygenUser = loadOxygenUserById(studentId, domainId);

        // Promote the student.
        studentOxygenUser.setAdmin(true);
        ofy().save().entity(studentOxygenUser);
    }
}
