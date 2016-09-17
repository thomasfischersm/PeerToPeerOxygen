package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.LoanerDeviceBean;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A client action to mark the current device as a studio loaner.
 */
public class MarkLoanerDeviceAction extends ClientAction {

    private final String friendlyName;

    public MarkLoanerDeviceAction(
            BinderForActions binder,
            String friendlyName,
            @Nullable CompletionCallback completionCallback) {

        super(binder, false, completionCallback);

        this.friendlyName = friendlyName;
    }

    @Override
    protected void executeAsync() throws IOException {
        LoanerDeviceBean loanerDevice = getBinder()
                .getApi()
                .markLoanerDevice(getBinder().getSessionId(), friendlyName)
                .execute();
        OxygenSharedPreferences.setLoanerDeviceId(getContext(), loanerDevice.getId());
    }
}
