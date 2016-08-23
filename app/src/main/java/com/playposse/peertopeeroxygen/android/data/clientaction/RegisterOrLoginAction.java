package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

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

    public RegisterOrLoginAction(BinderForActions binder) {
        super(binder);
    }

    public void registerOrLogin(
            final String accessToken,
            final DataService.SignInSuccessCallback signInSuccessCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String firebaseToken = FirebaseInstanceId.getInstance().getToken();
                    UserBean userBean =
                            getBinder().getApi()
                                    .registerOrLogin(accessToken, firebaseToken)
                                    .execute();
                    OxygenSharedPreferences.setSessionId(
                            getBinder().getApplicationContext(),
                            userBean.getSessionId());
                    signInSuccessCallback.onSuccess();
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to registerOrLogin.", ex);
                }
            }
        }).start();
    }
}
