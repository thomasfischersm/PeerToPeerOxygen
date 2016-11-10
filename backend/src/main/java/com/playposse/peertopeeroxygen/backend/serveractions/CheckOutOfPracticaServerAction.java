package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that removes a user from a practica as an attendee. Because the server doesn't
 * have a cron job to periodically clean up data from users who have left a practica, the devices
 * have to tell the server that the practica is over for each attendee.
 */
public class CheckOutOfPracticaServerAction extends ServerAction {

    public static void checkout(Long sessionId, Long practicaId, Long domainId)
            throws UnauthorizedException, BadRequestException, IOException {

        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser user = findOxygenUserByDomain(masterUser, domainId);

        user.setActivePracticaRef(null);
        ofy().save().entity(user);

        // Leave the user in the practica for historic record.
    }
}
