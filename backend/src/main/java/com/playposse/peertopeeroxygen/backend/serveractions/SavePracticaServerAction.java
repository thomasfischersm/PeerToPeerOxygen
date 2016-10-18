package com.playposse.peertopeeroxygen.backend.serveractions;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.firebase.SendPracticaUpdateServerAction;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that creates or updates a {@link Practica}.
 */

public class SavePracticaServerAction extends ServerAction {

    public static PracticaBean save(PracticaBean practicaBean) throws IOException {
        final Practica practica;
        if (practicaBean.getId() == null) {
            practica = practicaBean.toEntity();
        } else {
            practica = ofy().load().key(Key.create(Practica.class, practicaBean.getId())).now();
            practica.setName(practicaBean.getName());
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
        SendPracticaUpdateServerAction.sendPracticaUpdate(resultPracticaBean);
        return resultPracticaBean;
    }
}
