package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that unregisters a device as a studio loaner.
 */
public class UnmarkLoanerDeviceAction extends ServerAction {

    public static void unmarkLoanerDevice(Long loanerDeviceId) {
        ofy().delete().type(LoanerDevice.class).id(loanerDeviceId);
    }
}
