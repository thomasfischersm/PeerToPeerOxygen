package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

import java.io.IOException;

/**
 * An {@link ApiClientAction} that calls the cloud end point to create a private domain.
 */
public class CreatePrivateDomainClientAction extends ApiClientAction {

    private final String domainName;
    private final String domainDescription;
    private final Callback callback;

    private DomainBean domainBean;

    public CreatePrivateDomainClientAction(
            BinderForActions binder,
            String domainName,
            String domainDescription,
            Callback callback) {

        super(binder, false);

        this.domainName = domainName;
        this.domainDescription = domainDescription;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        domainBean = getApi()
                .createPrivateDomain(getSessionId(), domainName, domainDescription)
                .execute();
    }

    @Override
    protected void postExecute() {
        if (callback != null) {
            callback.onResult(domainBean);
        }
    }

    /**
     * A call back interface that notifies when the private domain has been created.
     */
    public interface Callback {

        void onResult(DomainBean domainBean);
    }
}
