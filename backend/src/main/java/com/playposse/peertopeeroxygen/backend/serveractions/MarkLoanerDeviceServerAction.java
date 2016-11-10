package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that marks a device as a loaner.
 */
public class MarkLoanerDeviceServerAction extends ServerAction {

    public static LoanerDeviceBean markLoanerDevice(
            Long sessionId,
            String friendlyName,
            Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Create refs
        Ref<OxygenUser> userRef = Ref.create(Key.create(OxygenUser.class, oxygenUser.getId()));
        Ref<Domain> domainRef = Ref.create(Key.create(Domain.class, domainId));

        LoanerDevice loanerDevice =
                new LoanerDevice(friendlyName, System.currentTimeMillis(), userRef, domainRef);
        ofy().save().entity(loanerDevice).now();

        LoanerDeviceBean loanerDeviceBean = new LoanerDeviceBean(loanerDevice);
        loanerDeviceBean.getLastUserBean();
        return loanerDeviceBean;
    }
}
