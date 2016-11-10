package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that unregisters a device as a studio loaner.
 */
public class UnmarkLoanerDeviceServerAction extends ServerAction {

    public static void unmarkLoanerDevice(Long sessionId, Long loanerDeviceId, Long domainId)
            throws UnauthorizedException, BadRequestException {
        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Verify domain of loaner device.
        LoanerDevice loanerDevice = ofy().load().type(LoanerDevice.class).id(loanerDeviceId).now();
        if (loanerDevice == null) {
            throw new BadRequestException("Loaner device could not be found: " + loanerDeviceId);
        }
        long actualLoanerDeviceId = loanerDevice.getDomainRef().getKey().getId();
        if (!domainId.equals(actualLoanerDeviceId)) {
            throw new BadRequestException("Tried to delete laoner device " + loanerDeviceId
                    + " from domain " + domainId + " but it was domain "
                    + actualLoanerDeviceId);
        }

        // Delete the loaner device.
        ofy().delete().type(LoanerDevice.class).id(loanerDeviceId);
    }
}
