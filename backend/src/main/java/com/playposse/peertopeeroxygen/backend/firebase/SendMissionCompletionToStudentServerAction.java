package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;

import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.getDomainId;

/**
 * A Firebase server action that notifies a student that he has been graduated from the mission.
 */

public class SendMissionCompletionToStudentServerAction extends FirebaseServerAction {

    private static final String MISSION_COMPLETION_TYPE = "missionCompletion";

    public static String sendMissionCompletionToStudent(
            String firebaseToken,
            OxygenUser buddy,
            Long missionId)
            throws IOException {

        Gson gson = new Gson();
        String buddyBeanJson = gson.toJson(stripCompletions(buddy));

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, MISSION_COMPLETION_TYPE);
        rootNode.put(BUDDY_BEAN, buddyBeanJson);
        rootNode.put(MISSION_KEY, missionId);

        return sendMessageToDevice(firebaseToken, rootNode, FirebasePriority.high);
    }
}
