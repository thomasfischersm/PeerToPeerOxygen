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

    public static final String PREFS_NAME = "OxygenPreferences";

    private static final String SESSION_KEY = "sessionId";

    /**
     * This flag is sunset. To avoid the name being reused and getting old preference data, this
     * flag should stay in the code as a reminder.
     */
    private static final String DEBUG_FLAG_KEY = "debug";
    private static final String LOANER_DEVICE_KEY = "loanerDeviceID";
    private static final String CURRENT_DOMAIN_ID_KEY = "currentDomainId";
    private static final String SUBSCRIBED_DOMAIN_IDS_KEY = "subscribedDomainIds";
    private static final String USER_EMAIL_KEY = "userEmail";
    private static final String FIREBASE_TOKEN_KEY = "firebaseToken";
    private static final String HAS_INTRO_DECK_BEEN_SHOWN_KEY = "hasIntroDeckBeenShown";
    private static final String DOMAIN_IDS_WITH_DISPLAYED_INTRO_KEY = "domainIdsWithDisplayedIntro";
    private static final String LAST_USER_ID_KEY = "lastUserId";

    private static final String NULL_STRING = "-1";

    public static Long getSessionId(Context context) {
        return getLong(context, SESSION_KEY);
    }

    public static void setSessionId(Context context, Long sessionId) {
        setLong(context, SESSION_KEY, sessionId);
        Log.i(LOG_CAT, "Session ID has been saved as " + sessionId);
    }

    public static boolean hasSessionId(Context context) {
        Long sessionId = getSessionId(context);
        return (sessionId != null) && (sessionId != -1);
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
        return getLongSet(context, SUBSCRIBED_DOMAIN_IDS_KEY);
    }

    public static void setSubscribedDomainIds(Context context, Set<Long> domainIds) {
        setLongSet(context, SUBSCRIBED_DOMAIN_IDS_KEY, domainIds);
    }

    public static void addSubscribedDomainId(Context context, Long domainId) {
        addValueToLongSet(context, SUBSCRIBED_DOMAIN_IDS_KEY, domainId);
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

    public static boolean hasIntroDeckBeenShown(Context context) {
        return getBoolean(context, HAS_INTRO_DECK_BEEN_SHOWN_KEY, false);
    }

    public static void setHasIntroDeckBeenShown(Context context, boolean hasIntroDeckBeenShown) {
        setBoolean(context, HAS_INTRO_DECK_BEEN_SHOWN_KEY, hasIntroDeckBeenShown);
    }

    public static Set<Long> getDomainIdsWithDisplayedIntro(Context context) {
        return getLongSet(context, DOMAIN_IDS_WITH_DISPLAYED_INTRO_KEY);
    }

    public static void setDomainIdsWithDisplayedIntro(Context context, Set<Long> domainIds) {
        setLongSet(context, DOMAIN_IDS_WITH_DISPLAYED_INTRO_KEY, domainIds);
    }

    public static void addDomainIdWithDisplayedIntro(Context context, Long domainId) {
        addValueToLongSet(context, DOMAIN_IDS_WITH_DISPLAYED_INTRO_KEY, domainId);
    }

    public static Long getLastUserId(Context context) {
        return getLong(context, LAST_USER_ID_KEY);
    }

    public static void setLastUserId(Context context, Long lastUserId) {
        setLong(context, LAST_USER_ID_KEY, lastUserId);
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
            sharedPreferences.edit().putString(key, value).commit();
        } else {
            sharedPreferences.edit().remove(key).commit();
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
            sharedPreferences.edit().putLong(key, value).commit();
        } else {
            sharedPreferences.edit().remove(key).commit();
        }
    }

    private static boolean getBoolean(Context context, String key, boolean defaultValue) {
        try {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(key, defaultValue);
        } catch (ClassCastException ex) {
            setBoolean(context, key, defaultValue);
            return false;
        }
    }

    private static void setBoolean(Context context, String key, boolean value) {
        Log.i(LOG_CAT, "Setting preference boolean for key " + key + " to " + value);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putBoolean(key, value)
                .commit();
    }

    private static Set<Long> getLongSet(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(key, null);

        if ((set == null) || (set.size() == 0)) {
            return new HashSet<>();
        }

        Set<Long> result = new HashSet<>(set.size());
        for (String value : set) {
            result.add(Long.valueOf(value));
        }
        return result;
    }

    private static void setLongSet(Context context, String key, Set<Long> set) {
        Set<String> stringSet = new HashSet<>(set.size());
        for (Long value : set) {
            stringSet.add(value.toString());
        }

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(key, stringSet).commit();
    }

    private static void addValueToLongSet(Context context, String key, Long value) {
        Set<Long> set = getLongSet(context, key);
        set.add(value);
        setLongSet(context, key, set);
    }
}
