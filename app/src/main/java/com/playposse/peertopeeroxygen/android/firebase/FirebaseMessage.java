package com.playposse.peertopeeroxygen.android.firebase;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Base message class for Firebase messages. Firebase sends the data in messages simply as
 * a map. Implementations of this class will strongly type the data.
 */
public abstract class FirebaseMessage {

    protected static final String FROM_STUDENT_ID = "fromStudentId";
    protected static final String FROM_STUDENT_BEAN = "fromStudentBean";
    protected static final String BUDDY_BEAN = "buddyBean";
    protected static final String STUDENT_BEAN = "studentBean";
    protected static final String MISSION_LADDER_KEY = "missionLadderId";
    protected static final String MISSION_TREE_KEY = "missionTreeId";
    protected static final String MISSION_KEY = "missionid";

    protected final Map<String, String> data;

    public FirebaseMessage(RemoteMessage message) {
        data = message.getData();
    }
}
