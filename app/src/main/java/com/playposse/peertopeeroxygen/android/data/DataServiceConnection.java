package com.playposse.peertopeeroxygen.android.data;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * A simple implementation of {@link ServiceConnection}.
 */
public class DataServiceConnection implements ServiceConnection {

    private static final String LOG_CAT = DataServiceConnection.class.getSimpleName();

    private final DataService.DataReceivedCallback dataReceivedCallback;

    private boolean bound = false;
    private DataService.LocalBinder localBinder;

    public DataServiceConnection(DataService.DataReceivedCallback dataReceivedCallback) {
        this.dataReceivedCallback = dataReceivedCallback;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        bound = true;
        localBinder = (DataService.LocalBinder) iBinder;
        localBinder.registerDataReceivedCallback(dataReceivedCallback);
        Log.i(LOG_CAT, "The service is now connected.");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

    public DataService.LocalBinder getLocalBinder() {
        return localBinder;
    }
}
