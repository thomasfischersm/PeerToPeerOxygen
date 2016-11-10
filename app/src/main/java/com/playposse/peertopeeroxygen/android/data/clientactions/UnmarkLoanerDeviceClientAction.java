package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A client action that unmarks the current device as a studio loaner.
 */
public class UnmarkLoanerDeviceClientAction extends ApiClientAction {

    private static final String LOG_CAT = UnmarkLoanerDeviceClientAction.class.getSimpleName();

    public UnmarkLoanerDeviceClientAction(
            BinderForActions binder,
            @Nullable CompletionCallback completionCallback) {

        super(binder, false, completionCallback);
    }

    @Override
    protected void executeAsync() throws IOException {
        Long loanerDeviceId = OxygenSharedPreferences.getLoanerDeviceId(getContext());
        if (loanerDeviceId == null) {
            Log.e(LOG_CAT, "Can't unmark device because the loanerDeviceId preference is null.");
            return;
        }

        OxygenSharedPreferences.setLoanerDeviceId(getContext(), null);
        getApi()
                .unmarkLoanerDevice(getSessionId(), loanerDeviceId, getDomainId())
                .execute();
    }
}
