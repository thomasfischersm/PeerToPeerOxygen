package com.playposse.peertopeeroxygen.android.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * An implementation of {@link FirebaseMessagingService} that receives messages from AppEngine. The
 * messages initiate the pairing process of a student and buddy.
 */
public class OxygenFirebaseMessagingService extends FirebaseMessagingService {

    public static final String LOG_CAT = OxygenFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(LOG_CAT, "Received Firebase message: " + remoteMessage);
        Log.i(LOG_CAT, "" + remoteMessage.getData());
    }
}
