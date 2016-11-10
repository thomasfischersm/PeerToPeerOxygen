package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Query;
import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.PeerToPeerOxygenEndPoint;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that returns a list of practicas.
 *
 * <p>Note: Future practica dates also includes currently ongoing practicas.
 */

public class GetPracticaServerAction extends ServerAction{

    private static final Logger log = Logger.getLogger(GetPracticaServerAction.class.getName());

    public enum PracticaDates {
        future,
        past,
    }

    public static List<PracticaBean> getPractica(
            Long sessionId,
            PracticaDates practicaDates,
            Long domainId)
            throws BadRequestException, UnauthorizedException {

        // Look up data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);

        Key<Domain> domainKey = Key.create(Domain.class, domainId);
        final List<Practica> practicas;
        switch (practicaDates) {
            case future:
                practicas = ofy().load()
                        .type(Practica.class)
                        .filter("domainRef =", domainKey)
                        .filter("end >=", System.currentTimeMillis())
                        .list();
                break;
            case past:
                practicas = ofy().load()
                        .type(Practica.class)
                        .filter("domainRef =", domainKey)
                        .filter("end <", System.currentTimeMillis())
                        .list();
                break;
            default:
                throw new BadRequestException("Unexpected practica date: " + practicaDates);
        }

        return convert(practicas, domainId);
    }

    private static List<PracticaBean> convert(List<Practica> practicas, Long domainId)
            throws BadRequestException {

        List<PracticaBean> practicaBeans = new ArrayList<>(practicas.size());
        if (practicas != null) {
            for (Practica practica : practicas) {
                verifyPracticaByDomain(practica, domainId);
                practicaBeans.add(new PracticaBean(practica));
            }
        }
        return practicaBeans;
    }
}
