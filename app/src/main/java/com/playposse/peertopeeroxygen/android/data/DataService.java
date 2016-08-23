package com.playposse.peertopeeroxygen.android.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.clientaction.BinderForActions;
import com.playposse.peertopeeroxygen.android.data.clientaction.DeleteMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.DeleteMissionLadderAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.DeleteMissionTreeAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.InviteBuddyToMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.MissionDataRetrieverAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.RegisterOrLoginAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.ReportMissionCompleteAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.SaveMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.SaveMissionLadderAction;
import com.playposse.peertopeeroxygen.android.data.clientaction.SaveMissionTreeAction;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

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
    private final DataRepository dataRepository = new DataRepository();

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
            callback.receiveData(dataRepository);
        }
    }

    private Long getSessionId() {
        return OxygenSharedPreferences.getSessionId(getApplicationContext());
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

            if ((dataRepository != null) && (dataRepository.getCompleteMissionDataBean() != null)) {
                callback.receiveData(dataRepository);
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
            if ((dataRepository == null) || (dataRepository.getCompleteMissionDataBean() == null)) {
                new MissionDataRetrieverAction(this).retrieveMissionData();
            }
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
        public DataRepository getDataRepository() {
            return dataRepository;
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

        public void reload() {
            new MissionDataRetrieverAction(this).retrieveMissionData();
        }
    }

    /**
     * A callback interface for activities to implement. The {@link #receiveData(DataRepository)} method is
     * called when the data is first loaded, when the data is refreshed, and when the callback is
     * first registered (if data is available).
     */
    public interface DataReceivedCallback {

        /**
         * Called when data is available. It's the job of this method to switch to the UI thread.
         *
         * @param dataRepository
         */
        void receiveData(DataRepository dataRepository);
    }

    /**
     * A callback interface to inform that the user has been signed in successfully. The session id
     * can be found in the shared preferences.
     */
    public interface SignInSuccessCallback {
        void onSuccess();
    }
}
