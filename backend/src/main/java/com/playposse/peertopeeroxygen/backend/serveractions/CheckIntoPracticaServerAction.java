package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.firebase.SendPracticaUserUpdateServerAction;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that checks a user into a practica.
 */
public class CheckIntoPracticaServerAction extends ServerAction {

    public static PracticaBean checkin(Long sessionId, Long practicaId)
            throws UnauthorizedException, BadRequestException, IOException {

        OxygenUser user = loadUserBySessionId(sessionId);
        Practica practica = ofy().load().type(Practica.class).id(practicaId).now();

        if (practica == null) {
            throw new BadRequestException("The practica " + practicaId + " doesn't exist.");
        }

        // Update user
        user.setActivePracticaRef(Ref.create(Key.create(Practica.class, practicaId)));
        ofy().save().entity(user);

        // Update practica
        if (!containsAttendee(practica, user)) {
            practica.getAttendeeUsers().add(Ref.create(Key.create(OxygenUser.class, user.getId())));
            ofy().save().entity(practica);
        }

        // Broadcast change to other practica attendees.
        SendPracticaUserUpdateServerAction.sendPracticaUserUpdate(user, practicaId);

        return new PracticaBean(practica);
    }

    private static boolean containsAttendee(Practica practica, OxygenUser user) {
        if (practica.getAttendeeUsers() != null) {
            for (Ref<OxygenUser> userRef : practica.getAttendeeUsers()) {
                if (user.getId().equals(userRef.getKey().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
