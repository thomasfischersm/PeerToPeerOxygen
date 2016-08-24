package com.playposse.peertopeeroxygen.android;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseMessagingService;

/**
 * An {@link Application} that starts the {@link DataService} when the application is started to
 * retrieve data right away.
 */
public class PeerToPeerOxygenApplication extends Application {

    private static final String LOG_CAT = PeerToPeerOxygenApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, DataService.class));

        // Fixes a timing bug where the OxygenFirebaseMessagingService isn't ready when a message
        // comes in.
        startService(new Intent(this, OxygenFirebaseMessagingService.class));
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_CAT, "Terminating PeerToPeerOxygenApplication");
        super.onTerminate();

        stopService(new Intent(this, DataService.class));
        stopService(new Intent(this, OxygenFirebaseMessagingService.class));
    }
}
