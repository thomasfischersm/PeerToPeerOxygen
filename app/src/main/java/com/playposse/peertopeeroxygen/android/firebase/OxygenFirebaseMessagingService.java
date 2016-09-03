package com.playposse.peertopeeroxygen.android.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.data.DataReceivedCallback;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.firebase.actions.FirebaseAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionCheckoutCompletionAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionCompletionAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionInvitationAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionSeniorInvitationAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.UpdatePointsAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link FirebaseMessagingService} that receives messages from AppEngine. The
 * messages initiate the pairing process of a student and buddy.
 */
public class OxygenFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_CAT = OxygenFirebaseMessagingService.class.getSimpleName();

    private static final String TYPE_KEY = "type";
    private static final String MISSION_INVITE_TYPE = "missionInvite";
    private static final String MISSION_SENIOR_INVITE_TYPE = "missionSeniorInvite";
    private static final String MISSION_COMPLETION_TYPE = "missionCompletion";
    private static final String MISSION_CHECKOUT_COMPLETION_TYPE = "missionCheckoutCompletion";
    private static final String UPDATE_STUDENT_POINTS_TYPE = "updateStudentPoints";

    protected DataServiceConnection dataServiceConnection;
    private List<FirebaseAction> pendingActions = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_CAT, "OxygenFirebaseMessagingService.onCreate is called");
        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(new EmptyDataReceivedCallback(), false);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(dataServiceConnection);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(LOG_CAT, "Received Firebase message: " + remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        Log.i(LOG_CAT, "" + data);

        if (data == null) {
            // Ignore. This might be a test message from the Firebase console.
            return;
        }

        switch (data.get(TYPE_KEY)) {
            case MISSION_INVITE_TYPE:
                execute(new MissionInvitationAction(remoteMessage));
                break;
            case MISSION_SENIOR_INVITE_TYPE:
                execute(new MissionSeniorInvitationAction(remoteMessage));
                break;
            case MISSION_COMPLETION_TYPE:
                execute(new MissionCompletionAction(remoteMessage));
                break;
            case MISSION_CHECKOUT_COMPLETION_TYPE:
                execute(new MissionCheckoutCompletionAction(remoteMessage));
                break;
            case UPDATE_STUDENT_POINTS_TYPE:
                execute(new UpdatePointsAction(remoteMessage));
                break;
            default:
                Log.w(LOG_CAT, "Received an unknown message type from Firebase: "
                        + data.get(TYPE_KEY));
        }
    }

    private void execute(FirebaseAction action) {
        pendingActions.add(action);
        executePendingActions();
    }

    private void executePendingActions() {
        if ((dataServiceConnection == null)
                || (dataServiceConnection.getLocalBinder() == null)
                || (dataServiceConnection.getLocalBinder().getDataRepository() == null)
                || (dataServiceConnection.getLocalBinder().getDataRepository().getCompleteMissionDataBean() == null)) {
            // Not ready to execute actions yet.
            return;
        }

        while (pendingActions.size() > 0) {
            FirebaseAction action = pendingActions.get(0);
            pendingActions.remove(action);
            action.execute(this, dataServiceConnection);
        }
    }

    private final class EmptyDataReceivedCallback implements DataReceivedCallback {
        @Override
        public void receiveData(DataRepository dataRepository) {
            executePendingActions();
        }

        @Override
        public void runOnUiThread(Runnable runnable) {
            runnable.run();
        }
    }
}
