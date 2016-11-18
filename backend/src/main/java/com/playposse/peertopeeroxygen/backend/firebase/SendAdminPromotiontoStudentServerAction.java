package com.playposse.peertopeeroxygen.backend.firebase;

import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Sends a Firebase message to the user to inform that the user has been promoted to or demote from
 * being an admin.
 */
public class SendAdminPromotionToStudentServerAction extends FirebaseServerAction {

    private static final String ADMIN_PROMOTION_TYPE = "adminPromotion";
    private static final String IS_ADMIN_KEY = "isAdmin";

    public static String sendAdminPromotionMessage(OxygenUser oxygenUser, boolean isAdmin)
            throws IOException {

        Domain domain = oxygenUser.getDomainRef().get();

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, ADMIN_PROMOTION_TYPE);
        rootNode.put(IS_ADMIN_KEY, isAdmin);
        rootNode.put(DOMAIN_KEY, domain.getId());
        rootNode.put(DOMAIN_NAME_KEY, domain.getName());

        String firebaseToken = oxygenUser.getMasterUserRef().get().getFirebaseToken();
        return sendMessageToDevice(firebaseToken, rootNode, FirebasePriority.high);
    }
}
