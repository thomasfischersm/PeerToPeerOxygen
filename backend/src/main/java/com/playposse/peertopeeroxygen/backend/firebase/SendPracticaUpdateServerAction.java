package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.api.server.spi.ConfiguredObjectMapper;
import com.google.appengine.repackaged.com.google.api.client.json.JsonGenerator;
import com.google.appengine.repackaged.com.google.api.client.json.jackson.JacksonFactory;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectWriter;
import com.playposse.peertopeeroxygen.backend.beans.PracticaBean;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Firebase server action that sends updated metainformation about a practica to the clients. This
 * can be a new practica or an update to an existing practica.
 */
public class SendPracticaUpdateServerAction extends FirebaseServerAction {

    private static final String PRACTICA_UPDATE_TYPE = "practicaUpdate";
    private static final String PRACTICA_BEAN = "practicaBean";

    public static String sendPracticaUpdate(PracticaBean practicaBean) throws IOException {
        String practicaJson = new Gson().toJson(practicaBean);

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, PRACTICA_UPDATE_TYPE);
        rootNode.put(PRACTICA_BEAN, practicaJson);

        return sendMessageToAllDevices(rootNode, FirebasePriority.normal, null);
    }
}