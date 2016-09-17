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
public class RegisterOrLoginAction extends ClientAction {

    private static final String LOG_CAT = RegisterOrLoginAction.class.getSimpleName();

    private final String accessToken;
    private final DataService.SignInSuccessCallback signInSuccessCallback;

    public RegisterOrLoginAction(
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
        getDataRepository().getCompleteMissionDataBean().setUserBean(userBean);
        signInSuccessCallback.onSuccess();
    }
}
