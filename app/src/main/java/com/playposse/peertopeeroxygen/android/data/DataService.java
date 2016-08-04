package com.playposse.peertopeeroxygen.android.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;

/**
 * A {@link Service} that retrieves the data from AppEngine and locally cashes it. (The second
 * part has to still be built.
 *
 * <p>Currently, the service should be started when the app starts up. Each activity should bind to it
 * to retrieve data.
 */
public class DataService extends Service {

    private static PeerToPeerOxygenApi peerToPeerOxygenEndPoint;

    @Override
    public void onCreate() {
        super.onCreate();

        peerToPeerOxygenEndPoint = new PeerToPeerOxygenApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setApplicationName("PeerToPeerOxygen")
                .setRootUrl("https://peertopeeroxygen.appspot.com/_ah/api/")
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * A {@link Runnable} that retrieves the mission data from the cloud.
     */
    private class MissionDataRetrieverRunnable implements Runnable {

        @Override
        public void run() {

        }
    }
}
