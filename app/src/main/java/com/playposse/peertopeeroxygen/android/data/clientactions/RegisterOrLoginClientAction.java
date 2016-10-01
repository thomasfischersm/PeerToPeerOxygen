package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
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
        UserBean userBean =
                getBinder().getApi()
                        .registerOrLogin(accessToken, firebaseToken)
                        .setLoanerDeviceId(loanerDeviceId)
                        .execute();
        OxygenSharedPreferences.setSessionId(getContext(), userBean.getSessionId());

        if ((getDataRepository() != null)
                && (getDataRepository().getCompleteMissionDataBean() != null)) {
            getDataRepository().getCompleteMissionDataBean().setUserBean(userBean);
        }
        signInSuccessCallback.onSuccess();
    }
}