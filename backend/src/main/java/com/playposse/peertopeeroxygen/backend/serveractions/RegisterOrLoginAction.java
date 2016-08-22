package com.playposse.peertopeeroxygen.backend.serveractions;

import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that logs a user in and register him/her as well if necessary.
 */
public class RegisterOrLoginAction extends ServerAction {

    private static final Logger log = Logger.getLogger(RegisterOrLoginAction.class.getName());

    public static UserBean registerOrLogin(String accessToken, String firebaseToken) {

        Long sessionId = new Random().nextLong();

        // Retrieve user data.
        User fbUser = fetchUserFromFaceBook(accessToken);
        List<OxygenUser> oxygenUsers = ofy()
                .load()
                .type(OxygenUser.class)
                .filter("fbProfileId", fbUser.getId())
                .list();

        // Register if necessary.
        OxygenUser oxygenUser;
        if (oxygenUsers.size() == 0) {
            oxygenUser = new OxygenUser(
                    sessionId,
                    fbUser.getId(),
                    firebaseToken,
                    fbUser.getName(),
                    fbUser.getFirstName(),
                    fbUser.getLastName(),
                    fbUser.getPicture().getUrl(),
                    System.currentTimeMillis(),
                    false);
            Key<OxygenUser> oxygenUserKey = ofy().save().entity(oxygenUser).now();
            oxygenUser.setId(oxygenUser.getId());
        } else {
            oxygenUser = oxygenUsers.get(0);
            if (oxygenUsers.size() > 1) {
                log.info("Found more than one OxygenUser entries for fbProfileId: "
                        + fbUser.getId());
            }
            oxygenUser.setSessionId(sessionId);
            oxygenUser.setFirebaseToken(firebaseToken);
            oxygenUser.setLastLogin(System.currentTimeMillis());
            ofy().save().entity(oxygenUser).now();
        }

        return new UserBean(oxygenUser);
    }

    private static User fetchUserFromFaceBook(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_7);
        return facebookClient.fetchObject(
                "me",
                User.class,
                Parameter.with("fields", "id,name,link,first_name, last_name,cover,picture.type(large)"));
    }
}
