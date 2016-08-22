package com.playposse.peertopeeroxygen.android.missiondependencies;

import android.content.Context;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

/**
 * A helper class that containst he business rules to check if a mission is available.
 */
public class MissionAvailabilityChecker {

    /**
     * An enum that determines the state of the mission. The state means what the user can or
     * cannot do with the mission.
     */
    public enum MissionAvailability {
        LOCKED,
        UNLOCKED,
        COMPLETED,
        TEACHABLE
    }

    /**
     * An enum that describes why the mission is locked, e.g. prerequisites missing or not enough
     * points of a certain type.
     */
    public enum LockReason {

    }

    public static MissionAvailability determineAvailability(
            MissionPlaceHolder holder,
            DataService.LocalBinder dataServiceBinder) {

        // TODO: Handle completion of mission boss. (The holder could be a MissionTree.)
        if (holder.getMissionBean() != null) {
            MissionCompletionBean missionCompletion =
                    dataServiceBinder.getMissionCompletion(holder.getMissionBean().getId());

            if (missionCompletion.getStudyCount() > 0) {
                return MissionAvailability.COMPLETED;
            }
        }

        for (MissionPlaceHolder child : holder.getChildren()) {
            MissionCompletionBean childCompletion =
                    dataServiceBinder.getMissionCompletion(child.getMissionBean().getId());
            if (childCompletion.getStudyCount() == 0) {
                return MissionAvailability.LOCKED;
            }
        }

        return MissionAvailability.UNLOCKED;
    }

    public static LockReason determineLockReason() {
        // TODO:
        return null;
    }

    /**
     * Returns a user friendly message to explain why the mission is locked.
     */
    public static String getLockReasonMessage(Context context, MissionPlaceHolder holder) {
        return context.getString(R.string.prerequisites_mission_lock_reason);
    }
}
