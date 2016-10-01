package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.student.StudentBuddyMissionActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

/**
 * A Firebase action that where the student receives notification that a mission has been completed.
 */
public class MissionCheckoutCompletionClientAction extends FirebaseClientAction {

    public MissionCheckoutCompletionClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    public void execute(RemoteMessage remoteMessage) {
        MissionCheckoutCompletionMessage message =
                new MissionCheckoutCompletionMessage(remoteMessage);

        // Look up data.
        MissionCheckoutCompletionMessage completionMessage =
                new MissionCheckoutCompletionMessage(remoteMessage);
        Long missionId = completionMessage.getMissionId();
        DataRepository dataRepository = getDataRepository();
        Long[] ids = dataRepository.getMissionPath(missionId);
        MissionBean missionBean = dataRepository.getMissionBean(ids[0], ids[1], ids[2]);

        // Update local mission completion to enable this user to teach the mission.
        MissionCompletionBean missionCompletion = dataRepository
                .getMissionCompletion(missionId);
        missionCompletion.setMentorCheckoutComplete(true);

        // Send a toast.
        Context context = getApplicationContext();
        String msg = String.format(
                context.getString(R.string.mission_checkout_completion_toast),
                missionBean.getName());
        sendToast(msg);

        // Refresh the mentor activity, so that the buddy can now graduate the student.
        Intent intent = ExtraConstants.createIntent(
                getApplicationContext(),
                StudentBuddyMissionActivity.class,
                ids[0],
                ids[1],
                message.getMissionId());
        intent.putExtra(ExtraConstants.EXTRA_STUDENT_BEAN, message.getStudentBean());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * A firebase message that tells the buddy that the senior buddy has successfully authorized
     * the buddy to teach the mission.
     */
    private static final class MissionCheckoutCompletionMessage extends FirebaseMessage {

        private MissionCheckoutCompletionMessage(RemoteMessage message) {
            super(message);
        }

        public Long getMissionId() {
            return Long.valueOf(data.get(MISSION_KEY));
        }

        private UserBeanParcelable getStudentBean() {
            return UserBeanParcelable.fromJson(data.get(STUDENT_BEAN));
        }
    }
}
