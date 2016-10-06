package com.playposse.peertopeeroxygen.android.model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;
import java.io.StringReader;

/**
 * Helper class that defines constants for passing in intents.
 */
public final class ExtraConstants {

    public static final String EXTRA_MISSION_LADDER_ID =
            "com.playposse.peertopeeroxygen.android.missionLadderId";
    public static final String EXTRA_MISSION_TREE_ID =
            "com.playposse.peertopeeroxygen.android.missionTreeId";
    public static final String EXTRA_MISSION_ID =
            "com.playposse.peertopeeroxygen.android.missionId";
    public static final String EXTRA_STUDENT_BEAN =
            "com.playposse.peertopeeroxygen.android.studentBean";
    public static final String EXTRA_BUDDY_BEAN =
            "com.playposse.peertopeeroxygen.android.buddyBean";
    public static final String EXTRA_USER_MISSION_ROLE =
            "com.playposse.peertopeeroxygen.android.userMissionRole";
    public static final String EXTRA_PRACTICA_ID =
            "com.playposse.peertopeeroxygen.android.practicaId";
    public static final String EXTRA_PRACTICA_BEAN =
            "com.playposse.peertopeeroxygen.android.practicaBean";

    public static Intent createIntent(
            Context context,
            Class<?> activityClass,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId) {

        Intent intent = new Intent(context, activityClass);
        intent.putExtra(EXTRA_MISSION_LADDER_ID, missionLadderId);
        intent.putExtra(EXTRA_MISSION_TREE_ID, missionTreeId);
        intent.putExtra(EXTRA_MISSION_ID, missionId);
        return intent;
    }

    public static Intent createIntent(
            Context context,
            Class<?> activityClass,
            PracticaBean practicaBean)
            throws IOException {

        Intent intent = new Intent(context, activityClass);
        intent.putExtra(EXTRA_PRACTICA_ID, practicaBean.getId());
        intent.putExtra(EXTRA_PRACTICA_BEAN, toJson(practicaBean));
        return intent;
    }

    public static String toJson(PracticaBean practicaBean) throws IOException {
        return practicaBean.getFactory().toString(practicaBean);
    }

    public static PracticaBean fromJson(@Nullable String json) throws IOException {
        if (json == null) {
            return null;
        }

        JsonObjectParser jsonParser = new JacksonFactory().createJsonObjectParser();
        return jsonParser.parseAndClose(new StringReader(json), PracticaBean.class);
    }
}
