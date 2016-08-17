package com.playposse.peertopeeroxygen.android.model;

import android.content.Context;
import android.content.Intent;

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
}
