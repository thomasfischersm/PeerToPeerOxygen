package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.support.annotation.Nullable;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;

/**
 * {@link ApiClientAction} to subscribe to a public domain.
 */
public class SubscribeToPublicDomainClientAction extends ApiClientAction {

    private final Long domainId;
    @Nullable private final Callback callback;

    private UserBean userBean;

    public SubscribeToPublicDomainClientAction(
            BinderForActions binder,
            Long domainId,
            @Nullable Callback callback) {
        super(binder, false);

        this.domainId = domainId;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        userBean = getApi().subscribeToPublicDomain(getSessionId(), domainId).execute();
    }

    @Override
    protected void postExecute() {
        if (callback != null) {
            callback.onResult(userBean);
        }
    }

    /**
     * Callback interface that is called when the call to the cloud completes.
     */
    public interface Callback {
        void onResult(UserBean userBean);
    }
}
