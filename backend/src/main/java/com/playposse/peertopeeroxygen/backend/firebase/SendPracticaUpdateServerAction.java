package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import org.json.JSONObject;

import java.io.IOException;

import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.getDomainId;

/**
 * Firebase server action that sends updated metainformation about a practica to the clients. This
 * can be a new practica or an update to an existing practica.
 */
public class SendPracticaUpdateServerAction extends FirebaseServerAction {

    private static final String PRACTICA_UPDATE_TYPE = "practicaUpdate";
    private static final String PRACTICA_BEAN = "practicaBean";

    public static String sendPracticaUpdate(Practica practica) throws IOException {
        PracticaBean practicaBean = new PracticaBean(practica);

        // Don't transmit this. It's too large for Firebase!
        practicaBean.setAttendeeUserBeans(null);

        String practicaJson = new Gson().toJson(practicaBean);

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, PRACTICA_UPDATE_TYPE);
        rootNode.put(PRACTICA_BEAN, practicaJson);

        return sendMessageToDomain(getDomainId(practica), rootNode, FirebasePriority.normal, null);
    }
}
