package com.playposse.peertopeeroxygen.android.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Service} that retrieves the data from AppEngine and locally cashes it. (The second
 * part has to still be built.
 * <p/>
 * <p>Currently, the service should be started when the app starts up. Each activity should bind to
 * it to retrieve data.
 */
public class DataService extends Service {

    private static PeerToPeerOxygenApi peerToPeerOxygenApi;

    private final List<DataReceivedCallback> dataReceivedCallbacks = new ArrayList<>();

    private CompleteMissionDataBean completeMissionDataBean;

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new MissionDataRetrieverRunnable()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createApiIfNeeded() {
        peerToPeerOxygenApi = new PeerToPeerOxygenApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null)
                .setApplicationName("PeerToPeerOxygen")
                .setRootUrl("https://peertopeeroxygen.appspot.com/_ah/api/")
                .build();
    }

    /**
     * A {@link Runnable} that retrieves the mission data from the cloud.
     */
    private class MissionDataRetrieverRunnable implements Runnable {

        @Override
        public void run() {
            createApiIfNeeded();

            try {
                completeMissionDataBean = peerToPeerOxygenApi.getMissionData().execute();

                for (DataReceivedCallback callback : dataReceivedCallbacks) {
                    callback.receiveData(completeMissionDataBean);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                String errorMsg = getString(R.string.network_error);
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * {@link IBinder} that returns a reference to this.
     */
    public class LocalBinder extends Binder {

        public DataService getService() {
            return DataService.this;
        }

        public void registerDataReceivedCallback(DataReceivedCallback callback) {
            dataReceivedCallbacks.add(callback);

            if (completeMissionDataBean != null) {
                callback.receiveData(completeMissionDataBean);
            }
        }
    }

    /**
     * A callback interface for activities to implement. The {@link #receiveData(CompleteMissionDataBean)} method is
     * called when the data is first loaded, when the data is refreshed, and when the callback is
     * first registered (if data is available).
     */
    public interface DataReceivedCallback {

        /**
         * Called when data is available. It's the job of this method to switch to the UI thread.
         * @param completeMissionDataBean
         */
        void receiveData(CompleteMissionDataBean completeMissionDataBean);
    }
}
