package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.data.CompleteMissionDataCache;

/**
 * A Firebase action that clears the local mission data cache because there is new data on the
 * server.
 */
public class InvalidateMissionDataClientAction extends FirebaseClientAction {

    private static final String LOG_CAT = InvalidateMissionDataClientAction.class.getSimpleName();

    public InvalidateMissionDataClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    protected void execute(RemoteMessage remoteMessage) {
        CompleteMissionDataCache.invalidate();
        Log.i(LOG_CAT, "Got Firebase message to invalidate mission cache.");
    }
}
