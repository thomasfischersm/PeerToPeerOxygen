package com.playposse.peertopeeroxygen.android.globalconfiguration;

import android.content.Context;
import android.content.Intent;

import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.student.StudentDomainSelectionActivity;
import com.playposse.peertopeeroxygen.android.student.StudentIntroductionDeckActivity;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;
import com.playposse.peertopeeroxygen.android.student.StudentMainActivity;

/**
 * A holder of business logic for re-directing the user to different {@link android.app.Activity}s.
 */
public class RedirectRouting {

    /**
     * Called when the app starts up to send the user to the first activity.
     */
    public static void onAppStartup(Context context) {
        Long sessionId = OxygenSharedPreferences.getSessionId(context);
        if ((sessionId != null) && (sessionId != -1)) {
            startActivity(context, StudentMainActivity.class);
        } else {
            startActivity(context, StudentLoginActivity.class);
        }
    }

    /**
     * Called when the user logs in successfully to decide the next activity.
     */
    public static void onSuccessfulLogin(Context context) {
        if (!OxygenSharedPreferences.hasIntroDeckBeenShown(context)) {
            startActivity(context, StudentIntroductionDeckActivity.class);
        } else if (OxygenSharedPreferences.getCurrentDomainId(context) == null) {
            startActivity(context, StudentDomainSelectionActivity.class);
        } else {
            startActivity(context, StudentMainActivity.class);
        }
    }

    /**
     * Called when the user finished going through the introduction slides to enter the app for
     * real.
     */
    public static void onIntroductionSlidesCompleted(Context context) {
        if (OxygenSharedPreferences.getCurrentDomainId(context) == null) {
            startActivity(context, StudentDomainSelectionActivity.class);
        } else {
            startActivity(context, StudentMainActivity.class);
        }
    }

    /**
     * Called when the app encounters a bad error and needs to do send the user to a place to start
     * over.
     */
    public static void onAppError(Context context) {
        // Clear the session id because it may be bad.
        OxygenSharedPreferences.setSessionId(context, (long) -1);

        Intent intent = new Intent(context, StudentLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Called when a working session id cannot be found.
     */
    public static void onLoginExpired() {

    }

    /**
     * Called when the user logs out.
     */
    public static void onLogout(Context context) {
        Intent intent = new Intent(context, StudentLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void startActivity(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
    }
}
