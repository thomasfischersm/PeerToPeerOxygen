package com.playposse.peertopeeroxygen.backend.firebase;

import org.json.JSONObject;

import java.io.IOException;

/**
 * A Firebase server action to notify all devices that the mission data has been updated.
 */

public class SendMissionDataInvalidationServerAction extends FirebaseServerAction {

    private static final String INVALIDATE_MISSION_DATA_TYPE = "invalidateMissionData";
    private static final String INVALIDATE_MISSION_DATA_COLLAPSE_KEY =
            "invalidateMissionDataCollapseKey";

    /**
     * Sends a message to all devices to tell them to invalidate the mission data cache.
     */
    public static String sendMissionDataInvalidation(Long domainId) throws IOException {
        JSONObject rootNode = new JSONObject();
        rootNode.put(TYPE_KEY, INVALIDATE_MISSION_DATA_TYPE);

        return sendMessageToDomain(
                domainId,
                rootNode,
                FirebasePriority.normal,
                INVALIDATE_MISSION_DATA_COLLAPSE_KEY);
    }
}
