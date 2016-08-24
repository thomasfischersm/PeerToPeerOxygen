package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * A class with utility methods for dealing with Firebase.
 */
public class FirebaseUtil {

    private static final Logger log = Logger.getLogger(FirebaseUtil.class.getName());

    private static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String APP_ID = "AIzaSyDMTDhV84ZdiacBCm9vN2z0q5BM3vJccgs";

    private static final String TYPE_KEY = "type";
    private static final String MISSION_INVITE_TYPE = "missionInvite";
    private static final String MISSION_COMPLETION_TYPE = "missionCompletion";
    private static final String UPDATE_STUDENT_POINTS_TYPE = "updateStudentPoints";

    private static final String FROM_STUDENT_ID = "fromStudentId";
    private static final String FROM_STUDENT_BEAN = "fromStudentBean";
    private static final String BUDDY_BEAN = "buddyBean";
    private static final String STUDENT_BEAN = "studentBean";
    private static final String MISSION_LADDER_KEY = "missionLadderId";
    private static final String MISSION_TREE_KEY = "missionTreeId";
    private static final String MISSION_KEY = "missionid";

    public static String sendMissionInviteToBuddy(
            String firebaseToken,
            UserBean studentBean,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId)
            throws IOException {

        Gson gson = new Gson();
        String studentBeanJson = gson.toJson(studentBean);
        log.info("The student translated to JSON: " + studentBeanJson);
        log.info("The student id is " + studentBean.getId());

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, MISSION_INVITE_TYPE);
        rootNode.put(FROM_STUDENT_ID, studentBean.getId());
        rootNode.put(FROM_STUDENT_BEAN, studentBeanJson);
        rootNode.put(MISSION_LADDER_KEY, missionLadderId);
        rootNode.put(MISSION_TREE_KEY, missionTreeId);
        rootNode.put(MISSION_KEY, missionId);

        return sendMessageToDevice(firebaseToken, rootNode);
    }

    public static String sendMissionCompletionToStudent(
            String firebaseToken,
            UserBean buddyBean,
            Long missionId)
            throws IOException {

        Gson gson = new Gson();
        String buddyBeanJson = gson.toJson(buddyBean);

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, MISSION_COMPLETION_TYPE);
        rootNode.put(BUDDY_BEAN, buddyBeanJson);
        rootNode.put(MISSION_KEY, missionId);

        return sendMessageToDevice(firebaseToken, rootNode);
    }

    public static String sendPointsUpdateToStudent(UserBean studentBean) throws IOException {
        Gson gson = new Gson();
        String studentBeanJson = gson.toJson(studentBean);

        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, UPDATE_STUDENT_POINTS_TYPE);
        rootNode.put(STUDENT_BEAN, studentBeanJson);

        return sendMessageToDevice(studentBean.getFirebaseToken(), rootNode);
    }

    private static String sendMessageToDevice(String firebaseToken, JSONObject data)
            throws IOException {

        JSONObject rootNode = new JSONObject();
        rootNode.put("to", firebaseToken);
        rootNode.put("data", data);

        log.info("Firebase payload: " + rootNode.toString());
        return sendMessage(rootNode.toString());
    }

    private static String sendMessage(String httpPayload) throws IOException {
        URL url = new URL(FIREBASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "key=" + APP_ID);
        connection.setRequestMethod("POST");

        OutputStream output = connection.getOutputStream();
        output.write(httpPayload.getBytes("UTF-8"));
        output.close();

        int httpResult = connection.getResponseCode();
        if (httpResult == HttpURLConnection.HTTP_OK) {
            return readStream(connection.getInputStream());
        } else {
            String errorMsg = readStream(connection.getErrorStream());
            throw new IOException("Request to Firebase failed: "
                    + connection.getResponseCode() + " "
                    + connection.getResponseMessage() + "\n"
                    + errorMsg);
        }
    }

    private static String readStream(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        reader.close();
        return sb.toString();
    }
}
