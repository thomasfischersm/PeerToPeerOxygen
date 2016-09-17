package com.playposse.peertopeeroxygen.android.util;

import android.content.Context;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;

/**
 * Utility for dealing with login out the current user.
 */
public class LogoutUtil {

    public static void logout(Context context) {
        OxygenSharedPreferences.setSessionId(context, null);
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(context, StudentLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
