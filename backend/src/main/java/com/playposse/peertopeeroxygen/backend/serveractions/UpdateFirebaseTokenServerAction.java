package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that updates the Firebase token. Firebase may update the token at any time in the
 * client.
 */
public class UpdateFirebaseTokenServerAction extends ServerAction {

    public static void updateFirebaseToken(Long sessionId, String firebaseToken)
            throws UnauthorizedException {

        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        masterUser.setFirebaseToken(firebaseToken);
        ofy().save().entity(masterUser).now();
    }
}
