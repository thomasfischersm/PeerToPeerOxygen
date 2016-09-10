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
import com.playposse.peertopeeroxygen.android.data.clientactions.AddPointsByAdminAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.BinderForActions;
import com.playposse.peertopeeroxygen.android.data.clientactions.DeleteMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.DeleteMissionLadderAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.DeleteMissionTreeAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetStudentRosterAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.InviteBuddyToMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.InviteSeniorBuddyToMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.MissionDataRetrieverAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.RegisterOrLoginAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.ReportMissionCheckoutCompleteAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.ReportMissionCompleteAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveMissionAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveMissionLadderAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveMissionTreeAction;
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
 * <p/>
 * <p>Currently, the service should be started when the app starts up. Each activity should bind to
 * it to retrieve data.
 */
public class DataService extends Service {

    private static final String LOG_CAT = DataService.class.getSimpleName();

    private static PeerToPeerOxygenApi peerToPeerOxygenApi;

    private final List<DataReceivedCallback> dataReceivedCallbacks = new ArrayList<>();

    private DataRepository dataRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (dataRepository == null) {
            dataRepository = new DataRepository();
            dataRepository.onStart(getApplicationContext());
        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        dataRepository.onStop(getApplicationContext());
    }

    private void showNetworkErrorToast() {
        String errorMsg = getString(R.string.network_error);
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * {@link IBinder} that returns a reference to this.
     */
    public class LocalBinder extends Binder implements BinderForActions {

        public void registerDataReceivedCallback(DataReceivedCallback callback) {
            dataReceivedCallbacks.add(callback);

            if ((dataRepository != null) && (dataRepository.getCompleteMissionDataBean() != null)) {
                callback.receiveData(dataRepository);
            }

            CompleteMissionDataCache.checkStale(this);
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
                CompleteMissionDataCache.getCompleteMissionDataBean(this, getApplicationContext());
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
        public DataRepository getDataRepository() {
            return dataRepository;
        }

        @Override
        public void makeDataReceivedCallbacks() {
            for (final DataReceivedCallback callback : new ArrayList<>(dataReceivedCallbacks)) {
                callback.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.receiveData(dataRepository);
                    }
                });
            }
        }

        @Override
        public Long getSessionId() {
            return OxygenSharedPreferences.getSessionId(getApplicationContext());
        }

        @Override
        public void redirectToLoginActivity() {
            // Clear the session id because it may be bad.
            OxygenSharedPreferences.setSessionId(getApplicationContext(), (long) -1);

            Context context = getApplicationContext();
            Intent intent = new Intent(context, StudentLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public void registerOrLogin(
                final String accessToken,
                final SignInSuccessCallback signInSuccessCallback) {

            new RegisterOrLoginAction(this, accessToken, signInSuccessCallback).execute();
        }

        public void save(final MissionLadderBean missionLadderBean) {
            new SaveMissionLadderAction(this, missionLadderBean).execute();
        }

        public void save(final Long missionLadderId, final MissionTreeBean missionTreeBean) {
            new SaveMissionTreeAction(this, missionLadderId, missionTreeBean).execute();
        }

        public void save(
                final Long missionLadderId,
                final Long missionTreeId,
                final MissionBean missionBean) {

            new SaveMissionAction(this, missionLadderId, missionTreeId, missionBean).execute();
        }

        public void deleteMissionLadder(final Long missionLadderId) {
            new DeleteMissionLadderAction(this, missionLadderId).execute();
        }

        public void deleteMissionTree(final Long missionLadderId, final Long missionTreeId) {
            new DeleteMissionTreeAction(this, missionLadderId, missionTreeId).execute();
        }

        public void deleteMission(
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new DeleteMissionAction(this, missionLadderId, missionTreeId, missionId).execute();
        }

        public void inviteBuddyToMission(
                final Long buddyId,
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new InviteBuddyToMissionAction(
                    this,
                    buddyId,
                    missionLadderId,
                    missionTreeId,
                    missionId)
                    .execute();
        }

        public void inviteSeniorBuddyToMission(
                final Long studentId,
                final Long seniorBuddyId,
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new InviteSeniorBuddyToMissionAction(
                    this,
                    studentId,
                    seniorBuddyId,
                    missionLadderId,
                    missionTreeId,
                    missionId)
                    .execute();
        }

        public void reportMissionComplete(Long studentId, Long missionId) {
            new ReportMissionCompleteAction(this, studentId, missionId).execute();
        }

        public void reportMissionCheckoutComplete(Long studentId, Long buddyId, Long missionId) {
            new ReportMissionCheckoutCompleteAction(this, studentId, buddyId, missionId).execute();
        }

        public void getStudentRoster(GetStudentRosterAction.StudentRosterCallback callback) {
            new GetStudentRosterAction(this, callback).execute();
        }

        public void addPointsByAdmin(Long studentId, String pointType, int addedPoints) {
            new AddPointsByAdminAction(this, getSessionId(), studentId, pointType, addedPoints)
                    .execute();
        }

        public void reload() {
            CompleteMissionDataCache.LoadRemotelyCallback cacheCallback =
                    new CompleteMissionDataCache.LoadRemotelyCallback(getApplicationContext());
            new MissionDataRetrieverAction(this, cacheCallback).execute();
        }
    }

    /**
     * A callback interface to inform that the user has been signed in successfully. The session id
     * can be found in the shared preferences.
     */
    public interface SignInSuccessCallback {
        void onSuccess();
    }
}
