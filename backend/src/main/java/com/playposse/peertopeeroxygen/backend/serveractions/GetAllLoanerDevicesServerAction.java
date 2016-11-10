package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.beans.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Server action that returns information about all loaner devices for admins.
 */
public class GetAllLoanerDevicesServerAction extends ServerAction {

    public static List<LoanerDeviceBean> getAllLoanerDevices(Long sessionid, Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Look up data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionid);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);

        // Do security check
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        Key<Domain> domainKey = Key.create(Domain.class, domainId);
        List<LoanerDevice> loanerDevices =
                ofy().load().type(LoanerDevice.class).filter("domainRef =", domainKey).list();
        List<LoanerDeviceBean> loanerDeviceBeans = new ArrayList<>(loanerDevices.size());
        for (LoanerDevice loanerDevice : loanerDevices) {
            loanerDeviceBeans.add(new LoanerDeviceBean(loanerDevice));
        }
        return loanerDeviceBeans;
    }
}
