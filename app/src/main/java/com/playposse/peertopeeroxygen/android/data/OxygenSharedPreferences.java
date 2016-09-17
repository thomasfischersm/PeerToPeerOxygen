package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class that provides access to shared preferences.
 */
public final class OxygenSharedPreferences {

    private static final String PREFS_NAME = "OxygenPreferences";

    private static final String SESSION_KEY = "sessionId";
    private static final String DEBUG_FLAG_KEY = "debug";
    private static final String LOANER_DEVICE_KEY = "loanderDeviceID";

    public static Long getSessionId(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SESSION_KEY, -1);
    }

    public static void setSessionId(Context context, Long sessionId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (sessionId != null) {
            sharedPreferences.edit().putLong(SESSION_KEY, sessionId).apply();
        } else {
            sharedPreferences.edit().remove(SESSION_KEY).apply();
        }
    }

    public static boolean getDebugFlag(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(DEBUG_FLAG_KEY, false)/* || true*/; // TODO: REMOVE LAST TRUE!
//        return true;
    }

    public static void setDebugFlag(Context context, boolean debugFlag) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(DEBUG_FLAG_KEY, debugFlag).apply();
    }

    public static Long getLoanerDeviceId(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long loanerDeviceId = sharedPreferences.getLong(LOANER_DEVICE_KEY, -1);
        return (loanerDeviceId != -1) ? loanerDeviceId : null;
    }

    public static void setLoanerDeviceId(Context context, Long loanerDeviceId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (loanerDeviceId != null) {
            sharedPreferences.edit().putLong(LOANER_DEVICE_KEY, loanerDeviceId).apply();
        } else {
            sharedPreferences.edit().remove(LOANER_DEVICE_KEY).apply();
        }
    }
}
