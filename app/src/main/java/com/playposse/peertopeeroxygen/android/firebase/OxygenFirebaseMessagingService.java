package com.playposse.peertopeeroxygen.android.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.data.DataReceivedCallback;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.firebase.actions.AdminPromotionClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.FirebaseClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.InvalidateMissionDataClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionCheckoutCompletionClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionCompletionClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionInvitationClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.MissionSeniorInvitationClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.PracticaUpdateClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.PracticaUserUpdateClientAction;
import com.playposse.peertopeeroxygen.android.firebase.actions.UpdatePointsClientAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static final String INVALIDATE_MISSION_DATA_TYPE = "invalidateMissionData";
    private static final String PRACTICA_UPDATE_TYPE = "practicaUpdate";
    private static final String PRACTICA_USER_UPDATE_TYPE = "practicaUserUpdate";
    private static final String ADMIN_PROMOTION_TYPE = "adminPromotion";

    private static final String ALL_DEVICES_TOPIC = "allDevices"; // TODO: Retire most usages, except for domain list updates.
    private static final String PRACTICA_FIREBASE_TOPIC_PREFIX = "practica-";
    private static final String DOMAIN_FIREBASE_TOPIC_PREFIX = "domain-";

    protected DataServiceConnection dataServiceConnection;
    private List<FirebaseClientAction> pendingActions = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_CAT, "OxygenFirebaseMessagingService.onCreate is called");
        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection =
                new DataServiceConnection(new EmptyDataReceivedCallback(), false, false);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);

        FirebaseMessaging.getInstance().subscribeToTopic(ALL_DEVICES_TOPIC);
        subscribeToDomainTopics();
    }

    private void subscribeToDomainTopics() {
        Set<Long> domainIds =
                OxygenSharedPreferences.getSubscribedDomainIds(getApplicationContext());
        if (domainIds != null) {
            for (Long domainId : domainIds) {
                subscribeToDomainTopic(domainId);
            }
        }
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
                execute(new MissionInvitationClientAction(remoteMessage));
                break;
            case MISSION_SENIOR_INVITE_TYPE:
                execute(new MissionSeniorInvitationClientAction(remoteMessage));
                break;
            case MISSION_COMPLETION_TYPE:
                execute(new MissionCompletionClientAction(remoteMessage));
                break;
            case MISSION_CHECKOUT_COMPLETION_TYPE:
                execute(new MissionCheckoutCompletionClientAction(remoteMessage));
                break;
            case UPDATE_STUDENT_POINTS_TYPE:
                execute(new UpdatePointsClientAction(remoteMessage));
                break;
            case INVALIDATE_MISSION_DATA_TYPE:
                execute(new InvalidateMissionDataClientAction(remoteMessage));
                break;
            case PRACTICA_UPDATE_TYPE:
                execute(new PracticaUpdateClientAction(remoteMessage));
                break;
            case PRACTICA_USER_UPDATE_TYPE:
                execute(new PracticaUserUpdateClientAction(remoteMessage));
                break;
            case ADMIN_PROMOTION_TYPE:
                execute(new AdminPromotionClientAction(remoteMessage));
                break;
            default:
                Log.w(LOG_CAT, "Received an unknown message type from Firebase: "
                        + data.get(TYPE_KEY));
        }
    }

    private void execute(FirebaseClientAction action) {
        pendingActions.add(action);
        executePendingActions();
    }

    private void executePendingActions() {
        Log.i(LOG_CAT, "Checking for pending Firebase actions.");
        if ((dataServiceConnection == null)
                || (dataServiceConnection.getLocalBinder() == null)
                || (dataServiceConnection.getLocalBinder().getDataRepository() == null)
                || (dataServiceConnection.getLocalBinder().getDataRepository().getCompleteMissionDataBean() == null)) {
            // Not ready to execute actions yet.
            return;
        }

        while (pendingActions.size() > 0) {
            FirebaseClientAction action = pendingActions.get(0);
            pendingActions.remove(action);
            action.execute(this, dataServiceConnection);
            Log.i(LOG_CAT, "Executed Firebase action " + action.getClass().getSimpleName());
        }
    }

    public static void subscribeToPracticaTopic(Long practicaId) {
        String topic = PRACTICA_FIREBASE_TOPIC_PREFIX +practicaId;
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public static void unsubscribeFromPracticaTopic(Long practicaId) {
        String topic = PRACTICA_FIREBASE_TOPIC_PREFIX +practicaId;
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public static void subscribeToDomainTopic(Long domainId) {
        String domain = DOMAIN_FIREBASE_TOPIC_PREFIX + domainId;
        FirebaseMessaging.getInstance().subscribeToTopic(domain);
    }

    public static void unsubscribeFromDomainTopic(Long domainId) {
        String domain = DOMAIN_FIREBASE_TOPIC_PREFIX + domainId;
        FirebaseMessaging.getInstance().unsubscribeFromTopic(domain);
    }

    private final class EmptyDataReceivedCallback
            implements DataReceivedCallback, DataServiceConnection.ServiceConnectionListener {

        @Override
        public void receiveData(DataRepository dataRepository) {
            executePendingActions();
        }

        @Override
        public void runOnUiThread(Runnable runnable) {
            runnable.run();
        }

        @Override
        public void onServiceConnected() {
            // Nothing to do.
        }
    }
}
