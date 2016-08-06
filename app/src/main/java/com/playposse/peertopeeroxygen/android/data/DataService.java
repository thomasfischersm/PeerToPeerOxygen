package com.playposse.peertopeeroxygen.android.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

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

    private static final String LOG_CAT = DataService.class.getSimpleName();

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
        return new LocalBinder();
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

    private void showNetworkErrorToast() {
        String errorMsg = getString(R.string.network_error);
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    private void makeDataReceivedCallbacks() {
        for (DataReceivedCallback callback : dataReceivedCallbacks) {
            callback.receiveData(completeMissionDataBean);
        }
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

                if (completeMissionDataBean.getMissionLadderBeans() == null) {
                    completeMissionDataBean.setMissionLadderBeans(
                            new ArrayList<MissionLadderBean>());
                }

                for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
                    if (missionLadderBean.getMissionTreeBeans() == null) {
                        missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
                    }
                }

                makeDataReceivedCallbacks();
                Log.i(LOG_CAT, "The data has been loaded.");
//                String msg = "Data has been loaded";
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                ex.printStackTrace();
                showNetworkErrorToast();
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

        public MissionLadderBean getMissionLadderBean(Long id) {
            for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
                if (missionLadderBean.getId().equals(id)) {
                    return missionLadderBean;
                }
            }
            return null;
        }

        public MissionTreeBean getMissionTreeBean(Long missionLadderId, Long missionTreeId) {
            for (MissionTreeBean missionTreeBean : getMissionLadderBean(missionLadderId).getMissionTreeBeans()) {
                if (missionTreeBean.getId().equals(missionTreeId)) {
                    return missionTreeBean;
                }
            }
            return null;
        }

        public void save(final MissionLadderBean missionLadderBean) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        boolean create = missionLadderBean.getId() == null;
                        MissionLadderBean result =
                                peerToPeerOxygenApi.saveMissionLadder(missionLadderBean).execute();
                        missionLadderBean.setId(result.getId()); // Save id for new entities.

                        // Update local data to avoid reloading data from the server.
                        if (create) {
                            completeMissionDataBean.getMissionLadderBeans().add(missionLadderBean);
                        }

                        makeDataReceivedCallbacks();

                        Log.i(LOG_CAT, "Mission ladder has been saved.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showNetworkErrorToast();
                    }
                }
            }).start();
        }

        public void save(final Long missionLadderId, final MissionTreeBean missionTreeBean) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        boolean create = missionTreeBean.getId() == null;
                        MissionTreeBean result =
                                peerToPeerOxygenApi.saveMissionTree(missionTreeBean).execute();
                        missionTreeBean.setId(result.getId()); // Save id for new entities.

                        // Update local data to avoid reloading data from the server.
                        if (create) {
                            MissionLadderBean missionLadderBean = getMissionLadderBean(missionLadderId);
                            missionLadderBean.getMissionTreeBeans().add(missionTreeBean);
                        }

                        makeDataReceivedCallbacks();

                        Log.i(LOG_CAT, "Mission tree has been saved.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showNetworkErrorToast();
                    }
                }
            }).start();
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
