package com.playposse.peertopeeroxygen.android.util;

import android.content.Context;
import android.content.Intent;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.globalconfiguration.RedirectRouting;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;

/**
 * Utility for dealing with login out the current user.
 */
public class LogoutUtil {

    public static void logout(Context context) {
        OxygenSharedPreferences.setSessionId(context, null);
        FacebookSdk.sdkInitialize(
                context,
                new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        LoginManager.getInstance().logOut();

                    }
                });

        RedirectRouting.onLogout(context);
    }
}
