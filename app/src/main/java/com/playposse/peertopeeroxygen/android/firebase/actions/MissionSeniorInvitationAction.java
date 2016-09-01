package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.student.StudentSeniorBuddyMissionActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

/**
 * A Firebased triggered mission received by a senior buddy when a buddy needs an experienced
 * buddy.
 */
public class MissionSeniorInvitationAction extends FirebaseAction {

    public MissionSeniorInvitationAction(
            Context applicationContext,
            DataServiceConnection dataServiceConnection) {

        super(applicationContext, dataServiceConnection);
    }

    public void handleSeniorMissionInvitation(RemoteMessage remoteMessage) {
        MissionSeniorInviteMessage message = new MissionSeniorInviteMessage(remoteMessage);
        MissionCompletionBean completion =
                getDataRepository().getMissionCompletion(message.getMissionId());
        Boolean isAdmin = getDataRepository().getUserBean().getAdmin();

        if (completion.getMentorCheckoutComplete() || isAdmin) {
            Intent intent = ExtraConstants.createIntent(
                    getApplicationContext(),
                    StudentSeniorBuddyMissionActivity.class,
                    message.getMissionLadderId(),
                    message.getMissionTreeId(),
                    message.getMissionId());
            intent.putExtra(ExtraConstants.EXTRA_STUDENT_BEAN, message.getStudentBean());
            intent.putExtra(ExtraConstants.EXTRA_BUDDY_BEAN, message.getBuddyBean());
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
                    getString(R.string.cant_supervise_mission_toast),
                    studentName,
                    missionBean.getName());
            sendToast(msg);
        }
    }


    /**
     * A Firebase message that invites the user to mentor another student in a mission.
     */
    private static final class MissionSeniorInviteMessage
            extends FirebaseMessage {

        public MissionSeniorInviteMessage(RemoteMessage message) {
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

        public UserBeanParcelable getBuddyBean() {
            return UserBeanParcelable.fromJson(data.get(BUDDY_BEAN));
        }
    }
}
