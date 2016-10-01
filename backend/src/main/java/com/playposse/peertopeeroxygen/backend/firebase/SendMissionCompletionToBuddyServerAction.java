package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;

/**
 * A Firebase server action to notify a buddy that he has been checked out for that mission.
 */

public class SendMissionCompletionToBuddyServerAction extends FirebaseServerAction {

    private static final String MISSION_CHECKOUT_COMPLETION_TYPE = "missionCheckoutCompletion";

    public static String sendMissionCompletionToBuddy(
            String firebaseToken,
            OxygenUser student,
            Long missionId)
            throws IOException {

        Gson gson = new Gson();
        String studentBeanJson = gson.toJson(stripCompletions(student));

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, MISSION_CHECKOUT_COMPLETION_TYPE);
        rootNode.put(STUDENT_BEAN, studentBeanJson);
        rootNode.put(MISSION_KEY, missionId);

        return sendMessageToDevice(firebaseToken, rootNode);
    }
}
