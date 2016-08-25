package com.playposse.peertopeeroxygen.android.missiondependencies;

import android.content.Context;

import com.playposse.peertopeeroxygen.android.MathUtil;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.JsonMap;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.Map;

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
        NONE,
        MISSING_MISSION,
        MISSING_POINTS,
    }

    public static MissionAvailability determineAvailability(
            MissionPlaceHolder holder,
            DataRepository dataRepository) {

        // Admins can always teach a mission.
        UserBean userBean = dataRepository.getUserBean();
        if (userBean.getAdmin() == true) {
            // Admin can access everything.
            return MissionAvailability.TEACHABLE;
        }

        // Check if the mission has already been completed.
        // TODO: Handle completion of mission boss. (The holder could be a MissionTree.)
        if (holder.getMissionBean() != null) {
            MissionCompletionBean missionCompletion =
                    dataRepository.getMissionCompletion(holder.getMissionBean().getId());

            if (missionCompletion.getStudyCount() > 0) {
                return MissionAvailability.COMPLETED;
            }
        }

        // Check if the mission has missing pre-requisites.
        if (!hasUserCompletedPrerequisiteMissions(holder, dataRepository))
            return MissionAvailability.LOCKED;

        // Check if the user has enough points.
        if (!doesUserHaveEnoughPoints(holder, userBean)) {
            return MissionAvailability.LOCKED;
        }

        return MissionAvailability.UNLOCKED;
    }

    private static boolean hasUserCompletedPrerequisiteMissions(
            MissionPlaceHolder holder,
            DataRepository dataRepository) {

        for (MissionPlaceHolder child : holder.getChildren()) {
            MissionCompletionBean childCompletion =
                    dataRepository.getMissionCompletion(child.getMissionBean().getId());
            if (childCompletion.getStudyCount() == 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean doesUserHaveEnoughPoints(MissionPlaceHolder holder, UserBean userBean) {
        if (holder.getMissionBean() != null) {
            JsonMap pointsMap = holder.getMissionBean().getPointCostMap();
            if (pointsMap != null) {
                for (Map.Entry<String, Object> entry : pointsMap.entrySet()) {
                    PointType pointType = PointType.valueOf(entry.getKey());
                    int pointCount = MathUtil.tryParseInt(entry.getValue().toString(), 0);
                    if (DataRepository.getPointByType(userBean, pointType) < pointCount) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static LockReason determineLockReason(
            DataRepository dataRepository,
            MissionPlaceHolder holder) {

        if (!hasUserCompletedPrerequisiteMissions(holder, dataRepository)) {
            return LockReason.MISSING_MISSION;
        } else if (!doesUserHaveEnoughPoints(holder, dataRepository.getUserBean())) {
            return LockReason.MISSING_POINTS;
        } else {
            return LockReason.NONE;
        }
    }

    /**
     * Returns a user friendly message to explain why the mission is locked.
     */
    public static String getLockReasonMessage(
            Context context,
            DataRepository dataRepository,
            MissionPlaceHolder holder) {

        LockReason lockReason = determineLockReason(dataRepository, holder);
        switch (lockReason) {
            case MISSING_MISSION:
                return context.getString(R.string.prerequisites_mission_lock_reason);
            case MISSING_POINTS:
                return context.getString(R.string.missing_points_lock_reason);
            default:
                throw new RuntimeException("Unexpected lock reason: " + lockReason);
        }
    }
}
