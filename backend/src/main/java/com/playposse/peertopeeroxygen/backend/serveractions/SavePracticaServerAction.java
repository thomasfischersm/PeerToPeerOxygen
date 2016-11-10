package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.firebase.SendPracticaUpdateServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that creates or updates a {@link Practica}.
 */

public class SavePracticaServerAction extends ServerAction {

    public static PracticaBean save(
            Long sessionId,
            PracticaBean practicaBean,
            Long domainId) throws IOException, UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Verify that the practica
        verifyPracticaByDomain(practicaBean, domainId);

        final Practica practica;
        if (practicaBean.getId() == null) {
            practica = practicaBean.toEntity();
        } else {
            practica = ofy().load().key(Key.create(Practica.class, practicaBean.getId())).now();
            practica.setName(practicaBean.getName());
            practica.setGreeting(practicaBean.getGreeting());
            practica.setAddress(practicaBean.getAddress());
            practica.setGpsLocation(practicaBean.getGpsLocation());
            practica.setStart(practicaBean.getStart());
            practica.setEnd(practicaBean.getEnd());
            practica.setTimezone(practicaBean.getTimezone());
            practica.setHostUser(Ref.create(Key.create(
                    OxygenUser.class,
                    practicaBean.getHostUserBean().getId())));
        }

        ofy().save().entities(practica).now();

        PracticaBean resultPracticaBean = new PracticaBean(practica);
        SendPracticaUpdateServerAction.sendPracticaUpdate(practica);
        return resultPracticaBean;
    }
}
