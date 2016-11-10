package com.playposse.peertopeeroxygen.android.firebase.actions;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.JsonMap;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.Map;

/**
 * A Firebase action that the student processes when receiving points from an admin.
 */
public class UpdatePointsClientAction extends FirebaseClientAction {

    public UpdatePointsClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    public void execute(RemoteMessage remoteMessage) {
        UpdatePointsMessage message = new UpdatePointsMessage(remoteMessage);
        UserBean userBean = getDataRepository().getUserBean();

        if (userBean.getPointsMap() == null) {
            userBean.setPointsMap(new JsonMap());
        }

        Map<String, Integer> pointMap = message.getStudentBean().getPointsMap();
        if (pointMap != null) {
            for (Map.Entry<String, Integer> entry : pointMap.entrySet()) {
                userBean.getPointsMap().put(entry.getKey(), entry.getValue());
            }
        }

        // Save changes to the davice storage.
        MissionDataManager.saveSync(getApplicationContext(), getLocalBinder());

        makeDataReceivedCallbacks();

        sendToast(getString(R.string.admin_sent_points_toast));
    }

    /**
     * A Firebase message that the AppEngine sends when an admin has updated the points of this
     * student.
     */
    private static final class UpdatePointsMessage extends FirebaseMessage {

        private UpdatePointsMessage(RemoteMessage message) {
            super(message);
        }

        private UserBeanParcelable getStudentBean() {
            return UserBeanParcelable.fromJson(data.get(STUDENT_BEAN));
        }
    }
}
