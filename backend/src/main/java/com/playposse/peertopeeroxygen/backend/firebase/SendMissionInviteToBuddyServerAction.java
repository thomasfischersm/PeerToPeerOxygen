package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A Firebase server action that invites a buddy to teach a mission.
 */

public class SendMissionInviteToBuddyServerAction extends FirebaseServerAction {

    private static final Logger log =
            Logger.getLogger(SendMissionInviteToBuddyServerAction.class.getName());

    private static final String MISSION_INVITE_TYPE = "missionInvite";

    public static String sendMissionInviteToBuddy(
            String firebaseToken,
            OxygenUser student,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId)
            throws IOException {

        Gson gson = new Gson();
        String studentBeanJson = gson.toJson(stripCompletions(student));
        log.info("The student translated to JSON: " + studentBeanJson);
        log.info("The student id is " + student.getId());

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, MISSION_INVITE_TYPE);
        rootNode.put(FROM_STUDENT_ID, student.getId());
        rootNode.put(FROM_STUDENT_BEAN, studentBeanJson);
        rootNode.put(MISSION_LADDER_KEY, missionLadderId);
        rootNode.put(MISSION_TREE_KEY, missionTreeId);
        rootNode.put(MISSION_KEY, missionId);

        return sendMessageToDevice(firebaseToken, rootNode, FirebasePriority.high);
    }
}
