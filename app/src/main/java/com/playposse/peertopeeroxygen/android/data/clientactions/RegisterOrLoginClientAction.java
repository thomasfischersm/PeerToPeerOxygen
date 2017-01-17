package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseMessagingService;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;

/**
 * A client action that calls the server to register or login when the user
 * logs in.
 */
public class RegisterOrLoginClientAction extends ApiClientAction {

    private static final String LOG_CAT = RegisterOrLoginClientAction.class.getSimpleName();

    private final String accessToken;
    private final DataService.SignInSuccessCallback signInSuccessCallback;

    public RegisterOrLoginClientAction(
            BinderForActions binder,
            String accessToken,
            DataService.SignInSuccessCallback signInSuccessCallback) {

        super(binder, false);

        this.accessToken = accessToken;
        this.signInSuccessCallback = signInSuccessCallback;
    }

    @Override
    protected void executeAsync() throws IOException {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Long loanerDeviceId = OxygenSharedPreferences.getLoanerDeviceId(getContext());

        Log.i(LOG_CAT, "Calling registerOrLogin with domain id: " + getDomainId());
        MasterUserBean masterUserBean =
                getBinder().getApi()
                        .registerOrLogin(accessToken, firebaseToken)
                        .setLoanerDeviceId(loanerDeviceId)
                        .setDomainId(getDomainId())
                        .execute();

        OxygenSharedPreferences.setSessionId(getContext(), masterUserBean.getSessionId());
        OxygenSharedPreferences.setUserEmail(getContext(), masterUserBean.getEmail());
        OxygenSharedPreferences.setFirebaseToken(getContext(), masterUserBean.getFirebaseToken());

        detectUserChange(masterUserBean);

        signInSuccessCallback.onSuccess();
    }

    private void detectUserChange(MasterUserBean masterUserBean) {
        Long lastUserId = OxygenSharedPreferences.getLastUserId(getContext());
        OxygenSharedPreferences.setLastUserId(getContext(), masterUserBean.getId());
        if ((lastUserId == null) || (lastUserId == -1)) {
            // No previous user
            return;
        }

        if (lastUserId.equals(masterUserBean.getId())) {
            // No user change.
            return;
        }

        // The user has changed. Clear the cache.
        MissionDataManager.invalidateAllDomains(getContext());
        OxygenSharedPreferences.setCurrentDomain(getContext(), -1L);
        OxygenFirebaseMessagingService.subscribeToDomainTopics(getContext());

        // TODO: Should subscribe to domains. It currently doesn't know which domains are subscribed.
        // TODO: Should deal with practica Firebase topic subscriptions.
    }
}
