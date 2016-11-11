package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class that provides access to shared preferences.
 */
public final class OxygenSharedPreferences {

    private static final String LOG_CAT = OxygenSharedPreferences.class.getSimpleName();

    private static final String PREFS_NAME = "OxygenPreferences";

    private static final String SESSION_KEY = "sessionId";
    private static final String DEBUG_FLAG_KEY = "debug";
    private static final String LOANER_DEVICE_KEY = "loanerDeviceID";
    private static final String CURRENT_DOMAIN_ID_KEY = "currentDomainId";
    private static final String SUBSCRIBED_DOMAIN_IDS_KEY = "subscribedDomainIds";
    private static final String USER_EMAIL_KEY = "userEmail";
    private static final String FIREBASE_TOKEN_KEY = "firebaseToken";

    private static final String NULL_STRING = "-1";

    public static Long getSessionId(Context context) {
        return getLong(context, SESSION_KEY);
    }

    public static void setSessionId(Context context, Long sessionId) {
        setLong(context, SESSION_KEY, sessionId);
    }

    public static boolean getDebugFlag(Context context) {

        try {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(DEBUG_FLAG_KEY, false) /*|| true*/; // TODO: REMOVE LAST TRUE!
        } catch (ClassCastException ex) {
            setDebugFlag(context, false);
            return true;
        }
//        return true;
    }

    public static void setDebugFlag(Context context, boolean debugFlag) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(DEBUG_FLAG_KEY, debugFlag).apply();
    }

    public static Long getLoanerDeviceId(Context context) {
        return getLong(context, LOANER_DEVICE_KEY);
    }

    public static void setLoanerDeviceId(Context context, Long loanerDeviceId) {
        setLong(context, LOANER_DEVICE_KEY, loanerDeviceId);
    }

    public static Long getCurrentDomainId(Context context) {
        return getLong(context, CURRENT_DOMAIN_ID_KEY);
    }

    public static void setCurrentDomain(Context context, Long domainId) {
        Log.i(LOG_CAT, "The current domain is set to " + domainId);
        setLong(context, CURRENT_DOMAIN_ID_KEY, domainId);
    }

    public static Set<Long> getSubscribedDomainIds(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> domainIds = sharedPreferences.getStringSet(SUBSCRIBED_DOMAIN_IDS_KEY, null);

        if ((domainIds == null) || (domainIds.size() == 0)) {
            return null;
        }

        Set<Long> result = new HashSet<>(domainIds.size());
        for (String domainId : domainIds) {
            result.add(Long.valueOf(domainId));
        }
        return result;
    }

    public static void setSubscribedDomainIds(Context context, Set<Long> domainIds) {
        Set<String> stringSet = new HashSet<>(domainIds.size());
        for (Long domainId : domainIds) {
            stringSet.add(domainId.toString());
        }

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(SUBSCRIBED_DOMAIN_IDS_KEY, stringSet).apply();
    }

    public static void addSubscribedDomainId(Context context, Long domainId) {
        Set<Long> subscribedDomainIds = getSubscribedDomainIds(context);
        subscribedDomainIds.add(domainId);
        setSubscribedDomainIds(context, subscribedDomainIds);
    }

    public static String getUserEmail(Context context) {
        return getString(context, USER_EMAIL_KEY);
    }

    public static void setUserEmail(Context context, String userEmail) {
        setString(context, USER_EMAIL_KEY, userEmail);
    }

    public static String getFirebaseToken(Context context) {
        return getString(context, FIREBASE_TOKEN_KEY);
    }

    public static void setFirebaseToken(Context context, String firebaseToken) {
        setString(context, FIREBASE_TOKEN_KEY, firebaseToken);
    }

    private static String getString(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(key, NULL_STRING);
        return (!NULL_STRING.equals(str)) ? str : null;
    }

    private static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (value != null) {
            sharedPreferences.edit().putString(key, value).apply();
        } else {
            sharedPreferences.edit().remove(key).apply();
        }
    }

    private static Long getLong(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Long value = sharedPreferences.getLong(key, -1);
        return (value != -1) ? value : null;
    }

    private static void setLong(Context context, String key, Long value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (value != null) {
            sharedPreferences.edit().putLong(key, value).apply();
        } else {
            sharedPreferences.edit().remove(key).apply();
        }
    }
}
