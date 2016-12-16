package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CombinedDomainBeans;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

/**
 * {@link ApiClientAction} that retrieves all public domains from the server.
 */
public class GetPublicDomainsClientAction extends ApiClientAction {

    private static final String LOG_CAT = GetPublicDomainsClientAction.class.getSimpleName();

    private final Callback callback;

    private CombinedDomainBeans combinedDomainBeans;

    public GetPublicDomainsClientAction(
            BinderForActions binder,
            Callback callback) {

        super(binder, false);

        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        Log.i(LOG_CAT, "GetPublicDomainsClientAction.executeAsync has been called.");
        combinedDomainBeans = getApi().getPublicDomains(getSessionId()).execute();
    }

    @Override
    protected void postExecute() {
        Log.i(LOG_CAT, "GetPublicDomainsClientAction.postExecute has been called.");
        callback.onResult(combinedDomainBeans);
    }

    /**
     * Callback interface that provides the data from the server.
     */
    public interface Callback {

        void onResult(CombinedDomainBeans combinedDomainBeans);
    }
}
