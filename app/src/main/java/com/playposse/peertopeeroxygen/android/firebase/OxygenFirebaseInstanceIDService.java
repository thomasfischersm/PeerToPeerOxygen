package com.playposse.peertopeeroxygen.android.firebase;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;

import java.io.IOException;

/**
 * A service that is required by Firebase to monitor changes to its access token.
 */
public class OxygenFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String LOG_CAT = OxygenFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        final String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(LOG_CAT, "Firebase updated its token.");

        final Long sessionId = OxygenSharedPreferences.getSessionId(getApplicationContext());
        if (sessionId != -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PeerToPeerOxygenApi peerToPeerOxygenApi = new PeerToPeerOxygenApi.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null)
                            .setApplicationName("PeerToPeerOxygen")
                            .setRootUrl("https://peertopeeroxygen.appspot.com/_ah/api/")
                            .build();

                    try {
                        peerToPeerOxygenApi.updateFirebaseToken(sessionId, firebaseToken);
                    } catch (IOException ex) {
                        Log.i(LOG_CAT, "Failed to send appEngine the new Firebase token.", ex);
                    }
                }
            }).start();
        }
    }
}
