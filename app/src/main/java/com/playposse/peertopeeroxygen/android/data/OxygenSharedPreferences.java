package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class that provides access to shared preferences.
 */
public final class OxygenSharedPreferences {

    public static final String PREFS_NAME = "OxygenPreferences";

    public static final String SESSION_KEY = "sessinId";

    public static Long getSessionId(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SESSION_KEY, -1);
    }

    public static void setSessionId(Context context, Long sessionId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(SESSION_KEY, sessionId).commit();
    }
}
