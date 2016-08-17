package com.playposse.peertopeeroxygen.android.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.student.StudentBuddyMissionActivity;

import java.util.Map;

/**
 * An implementation of {@link FirebaseMessagingService} that receives messages from AppEngine. The
 * messages initiate the pairing process of a student and buddy.
 */
public class OxygenFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_CAT = OxygenFirebaseMessagingService.class.getSimpleName();

    private static final String TYPE_KEY = "type";
    private static final String MISSION_INVITE_TYPE = "missionInvite";
    private static final String FROM_STUDENT_ID = "fromStudentId";
    private static final String FROM_STUDENT_BEAN = "fromStudentBean";
    private static final String MISSION_LADDER_KEY = "missionLadderId";
    private static final String MISSION_TREE_KEY = "missionTreeId";
    private static final String MISSION_KEY = "missionid";

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
                break;
            default:
                Log.w(LOG_CAT, "Received an unknown message type from Firebase: "
                        + data.get(TYPE_KEY));
        }
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
}
