package com.playposse.peertopeeroxygen.android.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Service} that retrieves the data from AppEngine and locally cashes it. (The second
 * part has to still be built.
 * <p>
 * <p>Currently, the service should be started when the app starts up. Each activity should bind to
 * it to retrieve data.
 */
public class DataService extends Service {

    private static final String LOG_CAT = DataService.class.getSimpleName();

    private static PeerToPeerOxygenApi peerToPeerOxygenApi;

    private final List<DataReceivedCallback> dataReceivedCallbacks = new ArrayList<>();

    private CompleteMissionDataBean completeMissionDataBean;

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

    private Long getSessionId() {
        return OxygenSharedPreferences.getSessionId(getApplicationContext());
    }

    private void debugDump() {
        Log.i(LOG_CAT, "Dumping user info");
        if (completeMissionDataBean.getUserBean() == null) {
            Log.i(LOG_CAT, "User bean is null");
        } else {
            Log.i(LOG_CAT, "User is " + completeMissionDataBean.getUserBean().getName());
        }

        Log.i(LOG_CAT, "Dumping complete mission data bean:");
        if (completeMissionDataBean.getMissionLadderBeans() != null) {
            for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
                Log.i(LOG_CAT, "- ladder (id: " + missionLadderBean.getId()
                        + ", name: " + missionLadderBean.getName()
                        + ", description: " + missionLadderBean.getDescription());
                if (missionLadderBean.getMissionTreeBeans() == null) {
                    Log.i(LOG_CAT, "-- mission tree: null");
                } else {
                    if (missionLadderBean.getMissionTreeBeans() != null) {
                        for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
                            Log.i(LOG_CAT, "-- mission tree (id: " + missionTreeBean.getId()
                                    + ", name: " + missionTreeBean.getName()
                                    + ", description: " + missionTreeBean.getDescription());
                            if (missionTreeBean.getMissionBeans() == null) {
                                Log.i(LOG_CAT, "--- mission: null");
                            } else {
                                if (missionTreeBean.getMissionBeans() != null) {
                                    for (MissionBean missionBean : missionTreeBean.getMissionBeans()) {
                                        Log.i(LOG_CAT, "--- mission: (id: " + missionBean.getId()
                                                + ", name: " + missionBean.getName()
                                                + ", student instruction: " + missionBean.getStudentInstruction()
                                                + ", buddy instruction: " + missionBean.getBuddyInstruction());
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
                Long sessionId = OxygenSharedPreferences.getSessionId(getApplicationContext());
                if (sessionId == -1) {
                    redirectToLoginActivity();
                }
                completeMissionDataBean = peerToPeerOxygenApi.getMissionData(sessionId).execute();

                Log.i(LOG_CAT, "BEFORE FIX");
                debugDump();

                fixNullLists();

                Log.i(LOG_CAT, "AFTER FIX");
                debugDump();

                makeDataReceivedCallbacks();
                Log.i(LOG_CAT, "The data has been loaded.");
//                String msg = "Data has been loaded";
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                ex.printStackTrace();
                redirectToLoginActivity();
            }
        }

        /**
         * Replaces null lists with an empty list.
         *
         * <p>Somehow, empty lists turn into null during transport. (JSON probably doesn't
         * differentiate.) To make things easier, all null lists are initialized with an empty list.
         */
        private void fixNullLists() {
            if (completeMissionDataBean.getMissionLadderBeans() == null) {
                completeMissionDataBean.setMissionLadderBeans(
                        new ArrayList<MissionLadderBean>());
            }

            for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
                if (missionLadderBean.getMissionTreeBeans() == null) {
                    missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
                }

                for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
                    if (missionTreeBean.getMissionBeans() == null) {
                        missionTreeBean.setMissionBeans(new ArrayList<MissionBean>());
                    }
                }
            }
        }
    }

    public void redirectToLoginActivity() {
        // Clear the session id because it may be bad.
        OxygenSharedPreferences.setSessionId(getApplicationContext(), (long) -1);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, StudentLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

        public void unregisterDataReceivedCallback(final DataReceivedCallback callback) {
            // Create a new thread to prevent concurrent modification exceptions to happen if this
            // is called from within a call to callbacks.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataReceivedCallbacks.remove(callback);
                }
            }).start();
        }

        public void init() {
            new Thread(new MissionDataRetrieverRunnable()).start();
        }

        public void registerOrLogin(
                final String accessToken,
                final SignInSuccessCallback signInSuccessCallback) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createApiIfNeeded();
                        UserBean userBean = peerToPeerOxygenApi.registerOrLogin(accessToken).execute();
                        OxygenSharedPreferences.setSessionId(
                                getApplicationContext(),
                                userBean.getSessionId());
                        signInSuccessCallback.onSuccess();
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to registerOrLogin.", ex);
                    }
                }
            }).start();
        }

        public UserBean getUserBean() {
            if (completeMissionDataBean != null) {
                return completeMissionDataBean.getUserBean();
            } else {
                return null;
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

        public MissionBean getMissionBean(
                Long missionLadderId,
                Long missionTreeId,
                Long missionId) {

            for (MissionBean missionBean : getMissionTreeBean(missionLadderId, missionTreeId).getMissionBeans()) {
                if (missionBean.getId().equals(missionId)) {
                    return missionBean;
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
                                peerToPeerOxygenApi
                                        .saveMissionLadder(getSessionId(), missionLadderBean)
                                        .execute();
                        missionLadderBean.setId(result.getId()); // Save id for new entities.

                        // Update local data to avoid reloading data from the server.
                        if (create) {
                            missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
                            completeMissionDataBean.getMissionLadderBeans().add(missionLadderBean);
                        }

                        makeDataReceivedCallbacks();

                        Log.i(LOG_CAT, "Mission ladder has been saved.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        redirectToLoginActivity();
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
                                peerToPeerOxygenApi
                                        .saveMissionTree(
                                                getSessionId(),
                                                missionLadderId,
                                                missionTreeBean)
                                        .execute();
                        missionTreeBean.setId(result.getId()); // Save id for new entities.

                        // Update local data to avoid reloading data from the server.
                        if (create) {
                            MissionLadderBean missionLadderBean =
                                    getMissionLadderBean(missionLadderId);
                            missionTreeBean.setMissionBeans(new ArrayList<MissionBean>());
                            missionLadderBean.getMissionTreeBeans().add(missionTreeBean);
                        }

                        makeDataReceivedCallbacks();

                        Log.i(LOG_CAT, "Mission tree has been saved.");
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to create new mission tree.", ex);
                        redirectToLoginActivity();
                    }
                }
            }).start();
        }

        public void save(
                final Long missionLadderId,
                final Long missionTreeId,
                final MissionBean missionBean) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean create = missionBean.getId() == null;

                        MissionBean result = peerToPeerOxygenApi.saveMission(
                                getSessionId(),
                                missionLadderId,
                                missionTreeId,
                                missionBean)
                                .execute();
                        missionBean.setId(result.getId());

                        if (create) {
                            MissionTreeBean missionTreeBean =
                                    getMissionTreeBean(missionLadderId, missionTreeId);
                            missionTreeBean.getMissionBeans().add(missionBean);
                        }

                        makeDataReceivedCallbacks();

                        Log.i(LOG_CAT, "Mission has been saved.");
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to save mission bean.", ex);
                        redirectToLoginActivity();
                    }
                }
            }).start();
        }

        public void deleteMissionLadder(final Long missionLadderId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        peerToPeerOxygenApi
                                .deleteMissionLadder(getSessionId(), missionLadderId)
                                .execute();
                        MissionLadderBean missionLadderBean = getMissionLadderBean(missionLadderId);
                        completeMissionDataBean.getMissionLadderBeans().remove(missionLadderBean);

                        makeDataReceivedCallbacks();
                        Log.i(LOG_CAT, "Completed deleting mission ladder: " + missionLadderId);
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to delete mission ladder.", ex);
                        redirectToLoginActivity();
                    }
                }
            }).start();
        }

        public void deleteMissionTree(final Long missionLadderId, final Long missionTreeId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MissionLadderBean missionLadderBean = getMissionLadderBean(missionLadderId);
                        MissionTreeBean missionTreeBean = getMissionTreeBean(missionLadderId, missionTreeId);
                        missionLadderBean.getMissionTreeBeans().remove(missionTreeBean);
                        peerToPeerOxygenApi
                                .saveMissionLadder(getSessionId(), missionLadderBean)
                                .execute();

                        makeDataReceivedCallbacks();
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to delete mission tree.", ex);
                        redirectToLoginActivity();
                    }
                }
            }).start();
        }


        public void deleteMission(
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MissionTreeBean missionTreeBean =
                                getMissionTreeBean(missionLadderId, missionTreeId);
                        MissionBean missionBean =
                                getMissionBean(missionLadderId, missionTreeId, missionId);
                        missionTreeBean.getMissionBeans().remove(missionBean);

                        peerToPeerOxygenApi
                                .deleteMission(
                                        getSessionId(),
                                        missionLadderId,
                                        missionTreeId,
                                        missionId)
                                .execute();

                        makeDataReceivedCallbacks();
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to delete mission.", ex);
                        redirectToLoginActivity();
                    }
                }
            }).start();
        }

        public void reload() {
            new Thread(new MissionDataRetrieverRunnable()).start();
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
         *
         * @param completeMissionDataBean
         */
        void receiveData(CompleteMissionDataBean completeMissionDataBean);
    }

    /**
     * A callback interface to inform that the user has been signed in successfully. The session id
     * can be found in the shared preferences.
     */
    public interface SignInSuccessCallback {
        void onSuccess();
    }
}
