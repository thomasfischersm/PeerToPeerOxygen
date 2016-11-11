package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;

/**
 * A Firebase server action that notifies a student that points have been awarded to his/her
 * account.
 */

public class SendPointsUpdateToStudentServerAction extends FirebaseServerAction {

    private static final String UPDATE_STUDENT_POINTS_TYPE = "updateStudentPoints";

    public static String sendPointsUpdateToStudent(OxygenUser student) throws IOException {
        Gson gson = new Gson();
        String studentBeanJson = gson.toJson(stripCompletions(student));

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, UPDATE_STUDENT_POINTS_TYPE);
        rootNode.put(STUDENT_BEAN, studentBeanJson);

        return sendMessageToDevice(student.getMasterUserRef().get().getFirebaseToken(), rootNode);
    }
}
