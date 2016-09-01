package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.student.StudentBuddyMissionActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

/**
 * A Firebased triggered mission received by a buddy when a student initiates a mission.
 */
public class MissionInvitationAction extends FirebaseAction {

    public MissionInvitationAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    public void execute(RemoteMessage remoteMessage) {

        MissionInviteMessage message = new MissionInviteMessage(remoteMessage);
        MissionCompletionBean completion =
                getDataRepository().getMissionCompletion(message.getMissionId());

        if (completion.getStudyComplete()) {
            Intent intent = ExtraConstants.createIntent(
                    getApplicationContext(),
                    StudentBuddyMissionActivity.class,
                    message.getMissionLadderId(),
                    message.getMissionTreeId(),
                    message.getMissionId());
            intent.putExtra(ExtraConstants.EXTRA_STUDENT_BEAN, message.getStudentBean());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            UserBeanParcelable studentBean = message.getStudentBean();
            String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
            MissionBean missionBean = getDataRepository().getMissionBean(
                    message.getMissionLadderId(),
                    message.getMissionTreeId(),
                    message.getMissionId());
            String msg = String.format(
                    getString(R.string.cant_mentor_mission_toast),
                    studentName,
                    missionBean.getName());
            sendToast(msg);
        }
    }


    /**
     * A Firebase message that invites the user to mentor another student in a mission.
     */
    private static final class MissionInviteMessage
            extends FirebaseMessage {

        public MissionInviteMessage(RemoteMessage message) {
            super(message);
        }

        public Long getMissionLadderId() {
            return Long.valueOf(data.get(MISSION_LADDER_KEY));
        }

        public Long getMissionTreeId() {
            return Long.valueOf(data.get(MISSION_TREE_KEY));
        }

        public Long getMissionId() {
            return Long.valueOf(data.get(MISSION_KEY));
        }

        public UserBeanParcelable getStudentBean() {
            return UserBeanParcelable.fromJson(data.get(FROM_STUDENT_BEAN));
        }
    }
}
