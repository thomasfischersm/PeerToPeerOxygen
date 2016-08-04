package com.playposse.peertopeeroxygen.android;

import android.app.Application;
import android.content.Intent;

import com.playposse.peertopeeroxygen.android.data.DataService;

/**
 * An {@link Application} that starts the {@link DataService} when the application is started to
 * retrieve data right away.
 */
public class PeerToPeerOxygenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, DataService.class));
    }
}
