package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Server action to retrieve the current information about a practica, specifically the attendee
 * status.
 *
 * <p>Note: The default strategy is to push practica changes via Firebase massages. The limitation
 * is that Firebase messages are limited in size. To get the full data of a practica, the HTTP
 * transport has to be used.
 */
public class GetPracticaByIdServerAction extends ServerAction {

    public static PracticaBean getPracticaById(Long practicaId) {
        Practica practica = ofy().load().type(Practica.class).id(practicaId).now();
        return new PracticaBean(practica);
    }
}
