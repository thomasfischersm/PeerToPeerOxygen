package com.playposse.peertopeeroxygen.backend.firebase;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.playposse.peertopeeroxygen.backend.beans.LevelCompletionBean;
import com.playposse.peertopeeroxygen.backend.beans.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * A class with utility methods for dealing with Firebase.
 */
public class FirebaseServerAction {

    public enum FirebasePriority {
        normal,
        high,
    }

    private static final Logger log = Logger.getLogger(FirebaseServerAction.class.getName());

    private static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String APP_ID = "AIzaSyDMTDhV84ZdiacBCm9vN2z0q5BM3vJccgs";

    private static final String ALL_DEVICES_DESTINATION = "/topics/allDevices";
    private static final String DOMAIN_FIREBASE_TOPIC_PREFIX = "/topics/domain-";

    protected static final String TYPE_KEY = "type";

    protected static final String FROM_STUDENT_ID = "fromStudentId";
    protected static final String FROM_STUDENT_BEAN = "fromStudentBean";
    protected static final String BUDDY_BEAN = "buddyBean";
    protected static final String STUDENT_BEAN = "studentBean";
    protected static final String MISSION_LADDER_KEY = "missionLadderId";
    protected static final String MISSION_TREE_KEY = "missionTreeId";
    protected static final String MISSION_KEY = "missionId";

    protected static String sendMessageToAllDevices(
            JSONObject data,
            FirebasePriority priority,
            @Nullable String collapseKey)
            throws IOException {
        return sendMessageToDevice(ALL_DEVICES_DESTINATION, data, priority, collapseKey);
    }

    protected  static String sendMessageToDomain(
            Long domainId,
            JSONObject data,
            FirebasePriority priority,
            @Nullable String collapseKey)
            throws IOException {

        String firebaseGroup = DOMAIN_FIREBASE_TOPIC_PREFIX + domainId;
        return sendMessageToDevice(firebaseGroup, data, priority, collapseKey);
    }

    protected static String sendMessageToDevice(
            String firebaseToken,
            JSONObject data)
            throws IOException {

        return sendMessageToDevice(firebaseToken, data, FirebasePriority.normal);
    }

    protected static String sendMessageToDevice(
            String firebaseToken,
            JSONObject data,
            FirebasePriority priority)
            throws IOException {

        return sendMessageToDevice(firebaseToken, data, priority, null);
    }

    private static String sendMessageToDevice(
            String firebaseToken,
            JSONObject data,
            FirebasePriority priority,
            @Nullable String collapseKey)
            throws IOException {

        JSONObject rootNode = new JSONObject();
        rootNode.put("to", firebaseToken);
        rootNode.put("data", data);
        rootNode.put("priority", priority.name());
        if (collapseKey != null) {
            rootNode.put("collapse_key", collapseKey);
        }

        log.info("Firebase payload: " + rootNode.toString());
        String response = sendMessage(rootNode.toString());
        log.info("Firebase response: " + response);
        return response;
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

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Removes level and mission completions. Firebase limits the size of the payload to 4K. The
     * completion data can easily make the bean size larger.
     */
    protected static UserBean stripCompletions(OxygenUser user) {
        UserBean userBean = new UserBean(user);
        userBean.setLevelCompletionBeans(new ArrayList<LevelCompletionBean>());
        userBean.setMissionCompletionBeans(new ArrayList<MissionCompletionBean>());
        return userBean;
    }
}
