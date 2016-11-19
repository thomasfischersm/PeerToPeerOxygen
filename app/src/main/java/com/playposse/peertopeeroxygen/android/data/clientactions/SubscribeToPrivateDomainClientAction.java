package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;

/**
 * Client action that validates the domain invitation code and subscribes the user to the private
 * domain.
 */
public class SubscribeToPrivateDomainClientAction extends ApiClientAction {

    private final String invitationCode;
    private final Callback callback;

    private UserBean userBean;
    private boolean hasFailed = false;

    public SubscribeToPrivateDomainClientAction(
            BinderForActions binder,
            String invitationCode,
            Callback callback) {
        super(binder, false);

        this.invitationCode = invitationCode;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        try {
            userBean = getApi().subscribeToPrivateDomain(getSessionId(), invitationCode).execute();
        } catch (IOException ex) {
            hasFailed = true;
        }
    }

    @Override
    protected void postExecute() {
        if (hasFailed) {
            callback.onError();
        } else {
            callback.onResult(userBean);
        }
    }

    /**
     * Callback interface that is called when the call to the cloud completes.
     */
    public interface Callback {
        void onResult(UserBean userBean);
        void onError();
    }
}
