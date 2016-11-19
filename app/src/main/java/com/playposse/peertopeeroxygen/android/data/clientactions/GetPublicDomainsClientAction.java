package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CombinedDomainBeans;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

/**
 * {@link ApiClientAction} that retrieves all public domains from the server.
 */
public class GetPublicDomainsClientAction extends ApiClientAction {

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
        combinedDomainBeans = getApi().getPublicDomains(getSessionId()).execute();
    }

    @Override
    protected void postExecute() {
        callback.onResult(combinedDomainBeans);
    }

    /**
     * Callback interface that provides the data from the server.
     */
    public interface Callback {

        void onResult(CombinedDomainBeans combinedDomainBeans);
    }
}
