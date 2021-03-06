package com.playposse.peertopeeroxygen.android.data;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * A simple implementation of {@link ServiceConnection}.
 */
public class DataServiceConnection implements ServiceConnection {

    private static final String LOG_CAT = DataServiceConnection.class.getSimpleName();

    private final ServiceConnectionListener dataReceivedCallback;
    private final boolean shouldAutoInit;
    private final boolean shouldRegisterCallback;
    private final boolean checkCacheStale;

    private boolean bound = false;
    private DataService.LocalBinder localBinder;

    public DataServiceConnection(
            ServiceConnectionListener dataReceivedCallback,
            boolean shouldAutoInit) {

        this(dataReceivedCallback, shouldAutoInit, true);
    }

    public DataServiceConnection(
            ServiceConnectionListener dataReceivedCallback,
            boolean shouldAutoInit,
            boolean checkCacheStale) {

        this(dataReceivedCallback, shouldAutoInit, checkCacheStale, true);
    }

    public DataServiceConnection(
            ServiceConnectionListener dataReceivedCallback,
            boolean shouldAutoInit,
            boolean checkCacheStale,
            boolean shouldRegisterCallback) {

        this.dataReceivedCallback = dataReceivedCallback;
        this.shouldAutoInit = shouldAutoInit;
        this.checkCacheStale = checkCacheStale;
        this.shouldRegisterCallback = shouldRegisterCallback;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        bound = true;
        localBinder = (DataService.LocalBinder) iBinder;

        if (shouldAutoInit) {
            localBinder.init();
        }

        if (shouldRegisterCallback) {
            localBinder.registerDataReceivedCallback(dataReceivedCallback, checkCacheStale);
        }

        dataReceivedCallback.onServiceConnected();

        Log.i(LOG_CAT, "The service is now connected.");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        localBinder.unregisterDataReceivedCallback(dataReceivedCallback);
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

    public DataService.LocalBinder getLocalBinder() {
        return localBinder;
    }

    /**
     * An interface that is called whenever the service has connected.
     */
    public interface ServiceConnectionListener extends DataReceivedCallback {

        void onServiceConnected();
    }
}
