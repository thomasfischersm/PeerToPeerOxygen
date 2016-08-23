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
import com.playposse.peertopeeroxygen.android.data.clientaction.BinderForActions;
import com.playposse.peertopeeroxygen.android.data.clientaction.DeleteMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.DeleteMissionLadderAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.DeleteMissionTreeAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.InviteBuddyToMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.RegisterOrLoginAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.ReportMissionCompleteAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.SaveMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.SaveMissionLadderAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.SaveMissionTreeAction;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
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
    public class LocalBinder extends Binder implements BinderForActions {

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

        @Override
        public PeerToPeerOxygenApi getApi() {
            createApiIfNeeded();
            return peerToPeerOxygenApi;
        }

        @Override
        public Context getApplicationContext() {
            return DataService.this.getApplicationContext();
        }

        @Override
        public Long getSessionId() {
            return DataService.this.getSessionId();
        }

        @Override
        public CompleteMissionDataBean getCompleteMissionDataBean() {
            return completeMissionDataBean;
        }

        @Override
        public void makeDataReceivedCallbacks() {
            DataService.this.makeDataReceivedCallbacks();
        }

        @Override
        public void redirectToLoginActivity() {
            DataService.this.redirectToLoginActivity();
        }

        public void registerOrLogin(
                final String accessToken,
                final SignInSuccessCallback signInSuccessCallback) {

            new RegisterOrLoginAction(this).registerOrLogin(accessToken, signInSuccessCallback);
        }

        public UserBean getUserBean() {
            if (completeMissionDataBean != null) {
                return completeMissionDataBean.getUserBean();
            } else {
                return null;
            }
        }

        @Override
        public MissionLadderBean getMissionLadderBean(Long id) {
            for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
                if (missionLadderBean.getId().equals(id)) {
                    return missionLadderBean;
                }
            }
            return null;
        }

        @Override
        public MissionTreeBean getMissionTreeBean(Long missionLadderId, Long missionTreeId) {
            for (MissionTreeBean missionTreeBean : getMissionLadderBean(missionLadderId).getMissionTreeBeans()) {
                if (missionTreeBean.getId().equals(missionTreeId)) {
                    return missionTreeBean;
                }
            }
            return null;
        }

        @Override
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
            new SaveMissionLadderAction(this).save(missionLadderBean);
        }

        public void save(final Long missionLadderId, final MissionTreeBean missionTreeBean) {
            new SaveMissionTreeAction(this).save(missionLadderId, missionTreeBean);
        }

        public void save(
                final Long missionLadderId,
                final Long missionTreeId,
                final MissionBean missionBean) {

            new SaveMissionAction(this).save(missionLadderId, missionTreeId, missionBean);
        }

        public void deleteMissionLadder(final Long missionLadderId) {
            new DeleteMissionLadderAction(this).deleteMissionLadder(missionLadderId);
        }

        public void deleteMissionTree(final Long missionLadderId, final Long missionTreeId) {
            new DeleteMissionTreeAction(this).deleteMissionTree(missionLadderId, missionTreeId);
        }

        public void deleteMission(
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new DeleteMissionAction(this).deleteMission(missionLadderId, missionTreeId, missionId);
        }

        public void inviteBuddyToMission(
                final Long buddyId,
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new InviteBuddyToMissionAction(this)
                    .inviteBuddyToMission(buddyId, missionLadderId, missionTreeId, missionId);
        }

        public void reportMissionComplete(final Long studentId, final Long missionId) {
            new ReportMissionCompleteAction(this).reportMissionComplete(studentId, missionId);
        }

        @Override
        public MissionCompletionBean getMissionCompletion(Long missionId) {
            if (getUserBean().getMissionCompletionBeans() != null) {
                for (MissionCompletionBean completionBean : getUserBean().getMissionCompletionBeans()) {
                    if (completionBean.getMissionId().equals(missionId)) {
                        return completionBean;
                    }
                }
            } else {
                getUserBean().setMissionCompletionBeans(new ArrayList<MissionCompletionBean>());
            }

            // Create a new one.
            MissionCompletionBean completionBean = new MissionCompletionBean();
            completionBean.setMissionId(missionId);
            completionBean.setStudyCount(0);
            completionBean.setMentorCount(0);
            getUserBean().getMissionCompletionBeans().add(completionBean);
            return completionBean;
        }

        /**
         * Finds the mission ladder id and mission tree id.
         *
         * @return Long[] An array with the ids for the mission ladder, mission tree, and mission.
         */
        public Long[] getMissionPath(Long missionId) {
            for (MissionLadderBean ladderBean : completeMissionDataBean.getMissionLadderBeans()) {
                for (MissionTreeBean treeBean : ladderBean.getMissionTreeBeans()) {
                    for (MissionBean missionBean : treeBean.getMissionBeans()) {
                        if (missionId.equals(missionBean.getId())) {
                            return new Long[]{ladderBean.getId(), treeBean.getId(), missionId};
                        }
                    }
                }
            }

            Log.e(LOG_CAT, "Couldn't find mission " + missionId);
            return null;
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
