package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that logs a user in and register him/her as well if necessary.
 */
public class RegisterOrLoginServerAction extends ServerAction {

    private static final Logger log = Logger.getLogger(RegisterOrLoginServerAction.class.getName());

    public static MasterUserBean registerOrLogin(
            String accessToken,
            String firebaseToken,
            @Nullable Long loanerDeviceId,
            @Nullable Long domainId) throws BadRequestException {

        Long sessionId = new Random().nextLong();
        Ref<Domain> domainRef = (domainId != null) ? RefUtil.createDomainRef(domainId) : null;

        // Check if the domain exists to prevent bad data.
        if (domainId != null) {
            Domain domain = ofy().load().type(Domain.class).id(domainId).now();
            if (domain == null) {
                throw new BadRequestException("The domain " + domainId + " doesn't exist.");
            }
        }

        // Retrieve user data.
        User fbUser = fetchUserFromFaceBook(accessToken);
        List<MasterUser> masterUsers = ofy()
                .load()
                .type(MasterUser.class)
                .filter("fbProfileId", fbUser.getId())
                .list();

        // Register if necessary.
        log.info("About to login or register user with FB id: " + fbUser.getId());
        final MasterUser masterUser;
        OxygenUser oxygenUser;
        if (masterUsers.size() == 0) {
            log.info("Proceeding to register user.");
            List<Ref<OxygenUser>> oxygenUserRefList = new ArrayList<>(1);
            Long masterUserId = new ObjectifyFactory().allocateId(MasterUser.class).getId();
            masterUser = new MasterUser(
                    masterUserId,
                    fbUser.getId(),
                    sessionId,
                    firebaseToken,
                    fbUser.getFirstName(),
                    fbUser.getLastName(),
                    fbUser.getName(),
                    fbUser.getPicture().getUrl(),
                    fbUser.getEmail(),
                    oxygenUserRefList);

            if (domainId != null) {
                oxygenUser = new OxygenUser(masterUser, false, domainRef);
                Key<OxygenUser> oxygenUserKey = ofy().save().entity(oxygenUser).now();
                Ref<OxygenUser> oxygenUserRef = Ref.create(oxygenUserKey);
                oxygenUserRefList.add(oxygenUserRef);
            }

            ofy().save().entity(masterUser).now();
        } else {
            log.info("Proceeding to log in user");
            masterUser = masterUsers.get(0);
            if (masterUsers.size() > 1) {
                log.info("Found more than one MasterUser entries for fbProfileId: "
                        + fbUser.getId());
            }

            if (domainId != null) {
                try {
                    oxygenUser = findOxygenUserByDomain(masterUser, domainId);
                    log.info("Found OxygenUser for domain " + domainId);
                } catch (BadRequestException ex) {
                    log.info("Creating new OxygenUser for the domain " + domainId);
                    oxygenUser = new OxygenUser(masterUser, false, domainRef);
                    Key<OxygenUser> oxygenUserKey = ofy().save().entity(oxygenUser).now();
                    if (masterUser.getDomainUserRefs() == null) {
                        masterUser.setDomainUserRefs(new ArrayList<Ref<OxygenUser>>());
                    }
                    masterUser.getDomainUserRefs().add(Ref.create(oxygenUserKey));
                }

                updateLoanerDevice(loanerDeviceId, oxygenUser);

            }

            masterUser.setSessionId(sessionId);
            masterUser.setFirebaseToken(firebaseToken);
            masterUser.setLastLogin(System.currentTimeMillis());
            masterUser.setEmail(fbUser.getEmail());
            ofy().save().entity(masterUser).now();
        }

        return new MasterUserBean(masterUser);
    }

    private static User fetchUserFromFaceBook(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_8);
        return facebookClient.fetchObject(
                "me",
                User.class,
                Parameter.with(
                        "fields",
                        "id,name,link,first_name,last_name,cover,picture.type(large),email"));
    }

    private static void updateLoanerDevice(@Nullable Long loanerDeviceId, OxygenUser oxygenUser) {
        if (loanerDeviceId == null) {
            return;
        }

        LoanerDevice loanerDevice = ofy().load().type(LoanerDevice.class).id(loanerDeviceId).now();
        loanerDevice.changeUser(oxygenUser);
        ofy().save().entity(loanerDevice);
    }
}
