package com.playposse.peertopeeroxygen.android.util;

import android.app.Activity;
import android.app.Application;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.playposse.peertopeeroxygen.android.PeerToPeerOxygenApplication;

/**
 * Helper class to make reporting information to Google Analytics less verbose.
 */
public class AnalyticsUtil {

    public static enum AnalyticsCategory {
        firebaseEvent,
        privateDomainCreation,
        studentGraduation,
        domainSelection,
    }

    public static enum ScreenName {
        introductionDeckSlide,
    }

    public static void reportEvent(
            Application defaultApp,
            AnalyticsCategory category,
            String action) {

        PeerToPeerOxygenApplication app = (PeerToPeerOxygenApplication) defaultApp;
        Tracker tracker = app.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category.name())
                .setAction(action)
                .build());
    }

    public static void reportScreenName(Application defaultApp, String screenName) {
        PeerToPeerOxygenApplication app = (PeerToPeerOxygenApplication) defaultApp;
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
