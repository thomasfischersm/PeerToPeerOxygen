package com.playposse.peertopeeroxygen.android.firebase;

import android.content.Context;
import android.os.AsyncTask;
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

    private static final String LOG_CAT = OxygenFirebaseInstanceIdService.class.getSimpleName();

    private boolean isRemoteCallPending = false;

    @Override
    public void onCreate() {
        super.onCreate();

        String actualFirebaseToken = FirebaseInstanceId.getInstance().getToken();
        String storedFirebaseToken =
                OxygenSharedPreferences.getFirebaseToken(getApplicationContext());

        if (!actualFirebaseToken.equals(storedFirebaseToken)) {
            updateFireBaseTokenInCloud(getApplicationContext());
        }
    }

    @Override
    public void onTokenRefresh() {
        updateFireBaseTokenInCloud(getApplicationContext());
    }

    private void updateFireBaseTokenInCloud(Context context) {
        if (!isRemoteCallPending) {
            isRemoteCallPending = true;
            new UpdateFireBaseTokenInCloudAsyncTask(context).execute();
        }
    }

    private class UpdateFireBaseTokenInCloudAsyncTask extends AsyncTask<Void, Void, String> {

        private final Context context;

        private UpdateFireBaseTokenInCloudAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String firebaseToken = FirebaseInstanceId.getInstance().getToken();
            Log.i(LOG_CAT, "Firebase updated its token: " + firebaseToken);

            final Long sessionId = OxygenSharedPreferences.getSessionId(context);
            if ((sessionId != null) && (sessionId != -1) && (firebaseToken != null)) {
                PeerToPeerOxygenApi peerToPeerOxygenApi = new PeerToPeerOxygenApi.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null)
                        .setApplicationName("PeerToPeerOxygen")
                        .setRootUrl("https://peertopeeroxygen.appspot.com/_ah/api/")
                        .build();

                try {
                    peerToPeerOxygenApi.updateFirebaseToken(sessionId, firebaseToken).execute();
                } catch (IOException ex) {
                    Log.i(LOG_CAT, "Failed to send appEngine the new Firebase token.", ex);
                    isRemoteCallPending = false;
                }
            }
            return firebaseToken;
        }

        @Override
        protected void onPostExecute(String firebaseToken) {
            OxygenSharedPreferences.setFirebaseToken(context, firebaseToken);
            isRemoteCallPending = false;
        }
    }
}
