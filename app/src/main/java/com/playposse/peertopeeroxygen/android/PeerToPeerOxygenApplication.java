package com.playposse.peertopeeroxygen.android;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.playposse.peertopeeroxygen.android.broadcastreceivers.ScreenOffBroadcastReceiver;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseInstanceIdService;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseMessagingService;

/**
 * An {@link Application} that starts the {@link DataService} when the application is started to
 * retrieve data right away.
 */
public class PeerToPeerOxygenApplication extends Application {

    private static final String LOG_CAT = PeerToPeerOxygenApplication.class.getSimpleName();

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_CAT, "PeerToPeerOxygenApplication.onCreate is called.");

        startService(new Intent(this, DataService.class));

        // Fixes a timing bug where the OxygenFirebaseMessagingService isn't ready when a message
        // comes in.
        startService(new Intent(this, OxygenFirebaseMessagingService.class));
        startService(new Intent(this, OxygenFirebaseInstanceIdService.class));

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenOffBroadcastReceiver(), intentFilter);
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_CAT, "Terminating PeerToPeerOxygenApplication");
        super.onTerminate();

        stopService(new Intent(this, DataService.class));
        stopService(new Intent(this, OxygenFirebaseMessagingService.class));
        stopService(new Intent(this, OxygenFirebaseInstanceIdService.class));
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}
