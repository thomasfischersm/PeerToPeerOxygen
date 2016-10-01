package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.LoanerDeviceBean;

import java.io.IOException;
import java.util.List;

/**
 * A client action that retrieves the metadata for all loaner devices.
 */
public class GetAllLoanerDevicesClientAction extends ApiClientAction {

    private final Callback callback;

    private List<LoanerDeviceBean> loanerDeviceBeanList;

    public GetAllLoanerDevicesClientAction(BinderForActions binder, Callback callback) {
        super(binder, false);

        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        loanerDeviceBeanList = getBinder()
                .getApi()
                .getAllLoanerDevices(getBinder().getSessionId())
                .execute()
                .getItems();
    }

    @Override
    protected void postExecute() {
        callback.onResult(loanerDeviceBeanList);
    }

    /**
     * A callback that provides all the mission stats.
     */
    public interface Callback {
        void onResult(List<LoanerDeviceBean> loanerDeviceBeanList);
    }
}
