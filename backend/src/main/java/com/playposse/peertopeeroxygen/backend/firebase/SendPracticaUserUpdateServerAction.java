package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.beans.PracticaUserBean;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.IOException;

/**
 * A Firebase server action that sends the meta information about a user to everyone in the
 * practica. This is called when the user first checks in and each time a mission is graduated.
 */
public class SendPracticaUserUpdateServerAction extends FirebaseServerAction {

    private static final String PRACTICA_USER_UPDATE_TYPE = "practicaUserUpdate";
    private static final String PRACTICA_USER_BEAN = "practicaUserBean";

    private static final String PRACTICA_FIREBASE_TOPIC_PREFIX = "/topics/practica-";

    public static String sendPracticaUserUpdate(OxygenUser user, Long practicaId)
            throws IOException {

        PracticaUserBean userBean = new PracticaUserBean(user);
        String practicaUserJson = new Gson().toJson(userBean);
        String firebaseGroup = PRACTICA_FIREBASE_TOPIC_PREFIX + practicaId;

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, PRACTICA_USER_UPDATE_TYPE);
        rootNode.put(PRACTICA_USER_BEAN, practicaUserJson);

        return sendMessageToDevice(firebaseGroup, rootNode, FirebasePriority.normal);
    }
}
