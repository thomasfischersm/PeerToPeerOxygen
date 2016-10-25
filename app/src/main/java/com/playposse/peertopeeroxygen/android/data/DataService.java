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
import com.playposse.peertopeeroxygen.android.data.clientactions.AddPointsByAdminClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.BinderForActions;
import com.playposse.peertopeeroxygen.android.data.clientactions.ApiClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.CheckIntoPracticaClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.DeleteMissionClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.DeleteMissionLadderClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.DeleteMissionTreeClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetAllLoanerDevicesClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetAllMissionFeedbackClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetAllMissionStatsClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetStudentRosterClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.InviteBuddyToMissionClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.InviteSeniorBuddyToMissionClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.MarkLoanerDeviceClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.MissionDataRetrieverClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.RegisterOrLoginClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.ReportMissionCheckoutCompleteClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.ReportMissionCompleteClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveMissionClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveMissionLadderClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveMissionTreeClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SavePracticaClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SubmitMissionFeedbackClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.UnmarkLoanerDeviceClientAction;
import com.playposse.peertopeeroxygen.android.student.StudentLoginActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

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
        LocalBinder localBinder = new LocalBinder();
        if (dataRepository == null) {
            dataRepository = new DataRepository();
            dataRepository.onStart(getApplicationContext(), localBinder);
        }

        return localBinder;
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
            registerDataReceivedCallback(callback, true);
        }

        public void registerDataReceivedCallback(
                DataReceivedCallback callback,
                boolean checkStale) {
            dataReceivedCallbacks.add(callback);

            if ((dataRepository != null) && (dataRepository.getCompleteMissionDataBean() != null)) {
                callback.receiveData(dataRepository);
            }

            if (checkStale) {
                CompleteMissionDataCache.checkStale(this);
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

            new RegisterOrLoginClientAction(this, accessToken, signInSuccessCallback).execute();
        }

        public void save(final MissionLadderBean missionLadderBean) {
            new SaveMissionLadderClientAction(this, missionLadderBean).execute();
        }

        public void save(final Long missionLadderId, final MissionTreeBean missionTreeBean) {
            new SaveMissionTreeClientAction(this, missionLadderId, missionTreeBean).execute();
        }

        public void save(
                final Long missionLadderId,
                final Long missionTreeId,
                final MissionBean missionBean) {

            new SaveMissionClientAction(this, missionLadderId, missionTreeId, missionBean).execute();
        }

        public void deleteMissionLadder(final Long missionLadderId) {
            new DeleteMissionLadderClientAction(this, missionLadderId).execute();
        }

        public void deleteMissionTree(final Long missionLadderId, final Long missionTreeId) {
            new DeleteMissionTreeClientAction(this, missionLadderId, missionTreeId).execute();
        }

        public void deleteMission(
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new DeleteMissionClientAction(this, missionLadderId, missionTreeId, missionId).execute();
        }

        public void inviteBuddyToMission(
                final Long buddyId,
                final Long missionLadderId,
                final Long missionTreeId,
                final Long missionId) {

            new InviteBuddyToMissionClientAction(
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

            new InviteSeniorBuddyToMissionClientAction(
                    this,
                    studentId,
                    seniorBuddyId,
                    missionLadderId,
                    missionTreeId,
                    missionId)
                    .execute();
        }

        public void reportMissionComplete(Long studentId, Long missionId) {
            new ReportMissionCompleteClientAction(this, studentId, missionId).execute();
        }

        public void reportMissionCheckoutComplete(Long studentId, Long buddyId, Long missionId) {
            new ReportMissionCheckoutCompleteClientAction(this, studentId, buddyId, missionId).execute();
        }

        public void getStudentRoster(GetStudentRosterClientAction.StudentRosterCallback callback) {
            new GetStudentRosterClientAction(this, callback).execute();
        }

        public void addPointsByAdmin(Long studentId, String pointType, int addedPoints) {
            new AddPointsByAdminClientAction(this, getSessionId(), studentId, pointType, addedPoints)
                    .execute();
        }

        public void submitMissionFeedback(Long missionId, int rating, @Nullable String comment) {
            new SubmitMissionFeedbackClientAction(this, missionId, rating, comment).execute();
        }

        public void getAllMissionFeedback(GetAllMissionFeedbackClientAction.Callback callback) {
            new GetAllMissionFeedbackClientAction(this, callback).execute();
        }

        public void getAllMissionStats(GetAllMissionStatsClientAction.Callback callback) {
            new GetAllMissionStatsClientAction(this, callback).execute();
        }

        public void markLoanerDevice(
                String friendlyName,
                @Nullable ApiClientAction.CompletionCallback completionCallback) {

            new MarkLoanerDeviceClientAction(this, friendlyName, completionCallback).execute();
        }

        public void unmarkLoanerDevice(
                @Nullable ApiClientAction.CompletionCallback completionCallback) {

            new UnmarkLoanerDeviceClientAction(this, completionCallback).execute();
        }

        public void getAllLoanerDevices(GetAllLoanerDevicesClientAction.Callback callback) {
            new GetAllLoanerDevicesClientAction(this, callback).execute();
        }

        public void getPractica(
                GetPracticaClientAction.PracticaDates practicaDates,
                GetPracticaClientAction.Callback callback) {
            new GetPracticaClientAction(this, practicaDates, callback).execute();
        }

        public void save(PracticaBean practicaBean) {
            new SavePracticaClientAction(this, practicaBean).execute();
        }

        public void checkIntoPractica(
                Long practicaId,
                CheckIntoPracticaClientAction.Callback callback) {

            new CheckIntoPracticaClientAction(this, practicaId, callback).execute();
        }

        public void reload() {
            CompleteMissionDataCache.LoadRemotelyCallback cacheCallback =
                    new CompleteMissionDataCache.LoadRemotelyCallback(getApplicationContext());
            new MissionDataRetrieverClientAction(this, cacheCallback).execute();
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
