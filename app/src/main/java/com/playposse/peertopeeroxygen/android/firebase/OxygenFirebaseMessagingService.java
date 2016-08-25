package com.playposse.peertopeeroxygen.android.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.MathUtil;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataReceivedCallback;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.student.StudentBuddyMissionActivity;
import com.playposse.peertopeeroxygen.android.student.StudentMissionTreeActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.JsonMap;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.Map;

/**
 * An implementation of {@link FirebaseMessagingService} that receives messages from AppEngine. The
 * messages initiate the pairing process of a student and buddy.
 */
public class OxygenFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_CAT = OxygenFirebaseMessagingService.class.getSimpleName();

    private static final String TYPE_KEY = "type";
    private static final String MISSION_INVITE_TYPE = "missionInvite";
    private static final String MISSION_COMPLETION_TYPE = "missionCompletion";
    private static final String UPDATE_STUDENT_POINTS_TYPE = "updateStudentPoints";

    private static final String FROM_STUDENT_ID = "fromStudentId";
    private static final String FROM_STUDENT_BEAN = "fromStudentBean";
    private static final String BUDDY_BEAN = "buddyBean";
    private static final String STUDENT_BEAN = "studentBean";
    private static final String MISSION_LADDER_KEY = "missionLadderId";
    private static final String MISSION_TREE_KEY = "missionTreeId";
    private static final String MISSION_KEY = "missionid";

    protected DataServiceConnection dataServiceConnection;

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
                handleMissionInvitation(remoteMessage);
                break;
            case MISSION_COMPLETION_TYPE:
                handleMissionCompletion(remoteMessage);
                break;
            case UPDATE_STUDENT_POINTS_TYPE:
                handleUpdatePoints(remoteMessage);
                break;
            default:
                Log.w(LOG_CAT, "Received an unknown message type from Firebase: "
                        + data.get(TYPE_KEY));
        }
    }

    private void handleMissionInvitation(RemoteMessage remoteMessage) {
        MissionInviteMessage message = new MissionInviteMessage(remoteMessage);
        Intent intent = ExtraConstants.createIntent(
                getApplicationContext(),
                StudentBuddyMissionActivity.class,
                message.getMissionLadderId(),
                message.getMissionTreeId(),
                message.getMissionId());
        intent.putExtra(ExtraConstants.EXTRA_STUDENT_BEAN, message.getStudentBean());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void handleMissionCompletion(RemoteMessage remoteMessage) {
        // Look up data.
        MissionCompletionMessage completionMessage =
                new MissionCompletionMessage(remoteMessage);
        UserBeanParcelable buddyBean = completionMessage.getBuddyBean();
        Long missionId = completionMessage.getMissionId();
        DataRepository dataRepository = dataServiceConnection
                .getLocalBinder()
                .getDataRepository();
        Long[] ids = dataRepository.getMissionPath(missionId);
        MissionBean missionBean = dataRepository.getMissionBean(ids[0], ids[1], ids[2]);

        // Update local mission completion count.
        MissionCompletionBean missionCompletion = dataRepository
                .getMissionCompletion(missionId);
        missionCompletion.setStudyCount(missionCompletion.getStudyCount() + 1);

        // Update local point counts.
        if (missionBean.getPointCostMap() != null) {
            for (Map.Entry<String, Object> entry : missionBean.getPointCostMap().entrySet()) {
                PointType pointType = PointType.valueOf(entry.getKey());
                int pointCount = 0 - MathUtil.tryParseInt(entry.getValue().toString(), 0);
                DataRepository.addPoints(dataRepository.getUserBean(), pointType, pointCount);
            }
        }

        // Send a toast.
        Context context = getApplicationContext();
        String message = String.format(
                context.getString(R.string.mission_completion_toast),
                missionBean.getName(),
                buddyBean.getFirstName() + " " + buddyBean.getLastName());
        sendToast(message);

        // Re-direct user back to the tree activity.
        Intent intent = ExtraConstants.createIntent(
                context,
                StudentMissionTreeActivity.class,
                ids[0], /* missionLadderId */
                ids[1], /* missionTreeId */
                null); /* missionId */
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void handleUpdatePoints(RemoteMessage remoteMessage) {
        UpdatePointsMessage message = new UpdatePointsMessage(remoteMessage);
        Log.i(LOG_CAT, "dataServiceConnection" + dataServiceConnection);
        Log.i(LOG_CAT, "dataServiceConnection.getLocalBinder()" + dataServiceConnection.getLocalBinder());
        Log.i(LOG_CAT, "dataServiceConnection.getLocalBinder().getDataRepository()" + dataServiceConnection.getLocalBinder().getDataRepository());
        Log.i(LOG_CAT, "dataServiceConnection.getLocalBinder().getDataRepository().getUserBean()" + dataServiceConnection.getLocalBinder().getDataRepository().getUserBean());
        UserBean userBean = dataServiceConnection.getLocalBinder().getDataRepository().getUserBean();

        if (userBean.getPointsMap() == null) {
            userBean.setPointsMap(new JsonMap());
        }

        Map<String, Integer> pointMap = message.getStudentBean().getPointsMap();
        if (pointMap != null) {
            for (Map.Entry<String, Integer> entry : pointMap.entrySet()) {
                userBean.getPointsMap().put(entry.getKey(), entry.getValue());
            }
        }

        dataServiceConnection.getLocalBinder().makeDataReceivedCallbacks();

        sendToast(getString(R.string.admin_sent_points_toast));
    }

    private void sendToast(final String message) {
        final Context context = getApplicationContext();
        Handler h = new Handler(context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Base message class for Firebase messages. Firebase sends the data in messages simply as
     * a map. Implementations of this class will strongly type the data.
     */
    private static abstract class FirebaseMessage {

        private final String type;
        protected final Map<String, String> data;

        public FirebaseMessage(RemoteMessage message) {
            data = message.getData();
            this.type = data.get(TYPE_KEY);
        }

        public String getType() {
            return type;
        }
    }

    /**
     * A Firebase message that invites the user to mentor another student in a mission.
     */
    private static final class MissionInviteMessage extends FirebaseMessage {

        public MissionInviteMessage(RemoteMessage message) {
            super(message);
        }

        public Long getMissionLadderId() {
            return new Long(data.get(MISSION_LADDER_KEY));
        }

        public Long getMissionTreeId() {
            return new Long(data.get(MISSION_TREE_KEY));
        }

        public Long getMissionId() {
            return new Long(data.get(MISSION_KEY));
        }

        public UserBeanParcelable getStudentBean() {
            return UserBeanParcelable.fromJson(data.get(FROM_STUDENT_BEAN));
        }
    }

    /**
     * A firebase message that tells the student that the buddy has marked a mission as completed.
     */
    private static final class MissionCompletionMessage extends FirebaseMessage {

        public MissionCompletionMessage(RemoteMessage message) {
            super(message);
        }

        public Long getMissionId() {
            return new Long(data.get(MISSION_KEY));
        }

        public UserBeanParcelable getBuddyBean() {
            return UserBeanParcelable.fromJson(data.get(BUDDY_BEAN));
        }
    }

    private static final class EmptyDataReceivedCallback implements DataReceivedCallback {
        @Override
        public void receiveData(DataRepository dataRepository) {
            // Nothing to do.
        }

        @Override
        public void runOnUiThread(Runnable runnable) {
            // Ignore.
        }
    }

    /**
     * A Firebase message that the AppEngine sends when an admin has updated the points of this
     * student.
     */
    private static final class UpdatePointsMessage extends FirebaseMessage {

        public UpdatePointsMessage(RemoteMessage message) {
            super(message);
        }

        public UserBeanParcelable getStudentBean() {
            return UserBeanParcelable.fromJson(data.get(STUDENT_BEAN));
        }
    }
}
