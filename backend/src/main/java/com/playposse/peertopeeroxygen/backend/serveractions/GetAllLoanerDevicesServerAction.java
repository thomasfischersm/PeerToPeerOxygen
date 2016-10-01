package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.beans.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Server action that returns information about all loaner devices for admins.
 */
public class GetAllLoanerDevicesServerAction extends ServerAction {

    public static List<LoanerDeviceBean> getAllLoanerDevices() {
        List<LoanerDevice> loanerDevices = ofy().load().type(LoanerDevice.class).list();
        List<LoanerDeviceBean> loanerDeviceBeans = new ArrayList<>(loanerDevices.size());
        for (LoanerDevice loanerDevice : loanerDevices) {
            loanerDeviceBeans.add(new LoanerDeviceBean(loanerDevice));
        }
        return loanerDeviceBeans;
    }
}
