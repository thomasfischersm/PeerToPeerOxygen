package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class that provides access to shared preferences.
 */
public final class OxygenSharedPreferences {

    private static final String PREFS_NAME = "OxygenPreferences";

    private static final String SESSION_KEY = "sessionId";
    private static final String DEBUG_FLAG_KEY = "debug";
    private static final String LOANER_DEVICE_KEY = "loanderDeviceID";
    private static final String CURRENT_DOMAIN_ID_KEY = "currentDomainId";
    private static final String SUBSCRIBED_DOMAIN_IDS_KEY = "subscribedDomainIds";

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

    public static Long getCurrentDomainId(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long domainId = sharedPreferences.getLong(CURRENT_DOMAIN_ID_KEY, -1);
        return (domainId != -1) ? domainId : null;
    }

    public static void setCurrentDomain(Context context, Long domainId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(CURRENT_DOMAIN_ID_KEY, domainId).apply();
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
}
