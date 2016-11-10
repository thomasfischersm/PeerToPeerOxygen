package com.playposse.peertopeeroxygen.android;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.broadcastreceivers.ScreenOffBroadcastReceiver;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseMessagingService;

import java.util.HashSet;

/**
 * An {@link Application} that starts the {@link DataService} when the application is started to
 * retrieve data right away.
 */
public class PeerToPeerOxygenApplication extends Application {

    private static final String LOG_CAT = PeerToPeerOxygenApplication.class.getSimpleName();

    public static Long DEFAULT_DOMAIN = 5652536396611584L;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_CAT, "PeerToPeerOxygenApplication.onCreate is called.");

        setDefaultDomain();

        startService(new Intent(this, DataService.class));

        // Fixes a timing bug where the OxygenFirebaseMessagingService isn't ready when a message
        // comes in.
        startService(new Intent(this, OxygenFirebaseMessagingService.class));

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenOffBroadcastReceiver(), intentFilter);
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_CAT, "Terminating PeerToPeerOxygenApplication");
        super.onTerminate();

        stopService(new Intent(this, DataService.class));
        stopService(new Intent(this, OxygenFirebaseMessagingService.class));
    }

    private void setDefaultDomain() {
        Long currentDomainId = OxygenSharedPreferences.getCurrentDomainId(getApplicationContext());
        if (currentDomainId == null) {
            OxygenSharedPreferences.setCurrentDomain(getApplicationContext(), DEFAULT_DOMAIN);
            HashSet<Long> subscribedDomainIds = new HashSet<>(1);
            subscribedDomainIds.add(DEFAULT_DOMAIN);
            OxygenSharedPreferences.setSubscribedDomainIds(
                    getApplicationContext(),
                    subscribedDomainIds);
        }
    }
}
