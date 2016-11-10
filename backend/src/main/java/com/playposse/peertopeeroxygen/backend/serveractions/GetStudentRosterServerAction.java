package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that downloads all the students.
 */
public class GetStudentRosterServerAction extends ServerAction {

    public List<UserBean> getStudentRoster(Long sessionId, Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Query data.
        Key<Domain> domainKey = Key.create(Domain.class, domainId);
        List<OxygenUser> users = ofy()
                .load()
                .type(OxygenUser.class)
                .filter("domainRef =", domainKey)
                .list();

        // Convert data to JSON.
        List<UserBean> userBeans = new ArrayList<>();
        for (OxygenUser user : users) {
            verifyUserByDomain(user, domainId);
            userBeans.add(new UserBean(user));
        }

        return userBeans;
    }
}
