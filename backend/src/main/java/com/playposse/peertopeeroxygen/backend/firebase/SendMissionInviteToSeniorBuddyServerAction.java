package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;

/**
 * A Firebase server action that invites a senior buddy to supervise a buddy teaching a mission.
 */

public class SendMissionInviteToSeniorBuddyServerAction extends FirebaseServerAction {

    private static final String MISSION_SENIOR_INVITE_TYPE = "missionSeniorInvite";

    public static String sendMissionInviteToSeniorBuddy(
            String firebaseToken,
            OxygenUser student,
            OxygenUser buddy,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId)
            throws IOException {

        Gson gson = new Gson();
        String studentBeanJson = gson.toJson(stripCompletions(student));
        String buddyBeanJson = gson.toJson(stripCompletions(buddy));

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, MISSION_SENIOR_INVITE_TYPE);
        rootNode.put(FROM_STUDENT_ID, student.getId());
        rootNode.put(FROM_STUDENT_BEAN, studentBeanJson);
        rootNode.put(BUDDY_BEAN, buddyBeanJson);
        rootNode.put(MISSION_LADDER_KEY, missionLadderId);
        rootNode.put(MISSION_TREE_KEY, missionTreeId);
        rootNode.put(MISSION_KEY, missionId);

        return sendMessageToDevice(firebaseToken, rootNode, FirebasePriority.high);
    }
}
