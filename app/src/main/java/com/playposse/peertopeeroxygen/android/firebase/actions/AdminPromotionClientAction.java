package com.playposse.peertopeeroxygen.android.firebase.actions;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.firebase.actions.FirebaseClientAction;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * A client side Firebase action that handles notifications from the cloud that this user has been
 * promoted to or demote from the admin role.
 */
public class AdminPromotionClientAction extends FirebaseClientAction {

    public AdminPromotionClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    protected void execute(RemoteMessage remoteMessage) {
        AdminPromotionMessage message = new AdminPromotionMessage(remoteMessage);

        Long domainId = OxygenSharedPreferences.getCurrentDomainId(getApplicationContext());
        if (message.getDomainId().equals(domainId)) {
            // Update user for current domain.
            UserBean userBean = getDataRepository().getUserBean();
            userBean.setAdmin(message.isAdmin());
        } else {
            // Update user for domain in background.
            MissionDataManager.invalidate(getApplicationContext(), message.getDomainId());
        }

        makeDataReceivedCallbacks();

        if (message.isAdmin()) {
            ToastUtil.sendToast(
                    getApplicationContext(),
                    R.string.admin_promotion_toast,
                    message.getDomainName());
        } else {
            ToastUtil.sendToast(
                    getApplicationContext(),
                    R.string.admin_demotion_toast,
                    message.getDomainName());
        }
    }

    /**
     * A Firebase message that describes how a student got promoted to or demoted from the admin
     * role.
     */
    private static final class AdminPromotionMessage extends FirebaseMessage {

        private static final String IS_ADMIN_KEY = "isAdmin";

        private AdminPromotionMessage(RemoteMessage message) {
            super(message);
        }

        private boolean isAdmin() {
            return Boolean.valueOf(data.get(IS_ADMIN_KEY));
        }

        private Long getDomainId() {
            return Long.valueOf(data.get(DOMAIN_KEY));
        }

        private String getDomainName() {
            return String.valueOf(data.get(DOMAIN_NAME_KEY));
        }
    }
}
