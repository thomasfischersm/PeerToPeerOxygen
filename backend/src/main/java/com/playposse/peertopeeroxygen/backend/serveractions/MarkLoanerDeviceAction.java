package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that marks a device as a loaner.
 */
public class MarkLoanerDeviceAction extends ServerAction {

    public static LoanerDeviceBean markLoanerDevice(Long sessionId, String friendlyName)
            throws UnauthorizedException {

        OxygenUser user = loadUserBySessionId(sessionId);
        Ref<OxygenUser> userRef = Ref.create(Key.create(OxygenUser.class, user.getId()));

        LoanerDevice loanerDevice =
                new LoanerDevice(friendlyName, System.currentTimeMillis(), userRef);
        ofy().save().entity(loanerDevice).now();

        LoanerDeviceBean loanerDeviceBean = new LoanerDeviceBean(loanerDevice);
        stripForSafety(loanerDeviceBean.getLastUserBean());
        return loanerDeviceBean;
    }
}
