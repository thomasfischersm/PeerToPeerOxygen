package com.playposse.peertopeeroxygen.android.missiondependencies;

import android.content.Context;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.util.MathUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.JsonMap;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
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
        LEVEL_LOCKED,
        MISSING_MISSION,
        MISSING_POINTS,
    }

    public static MissionAvailability determineAvailability(
            MissionPlaceHolder holder,
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            DataRepository dataRepository) {

        // Admins can always teach a mission.
        UserBean userBean = dataRepository.getUserBean();
        if (userBean.getAdmin()) {
            // Admin can access everything.
            return MissionAvailability.TEACHABLE;
        }

        // Check if the mission has already been completed.
        MissionCompletionBean missionCompletion =
                dataRepository.getMissionCompletion(holder.getMissionBean().getId());

        if (missionCompletion.getMentorCheckoutComplete()) {
            return MissionAvailability.TEACHABLE;
        } else if (missionCompletion.getStudyComplete()) {
            return MissionAvailability.COMPLETED;
        }

        // Check if the level ha sbeen unlocked.
        if (isLevelLocked(missionLadderId, missionTreeBean, dataRepository)) {
            return MissionAvailability.LOCKED;
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

    private static boolean isLevelLocked(
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            DataRepository dataRepository) {

        // The first level is always unlocked.
        if (missionTreeBean.getLevel() <= 1) {
            return false;
        }

        int previousLevel = missionTreeBean.getLevel() - 1;
        MissionTreeBean previousMissionTreeBean = dataRepository.getMissionTreeBeanByLevel(missionLadderId, previousLevel);
        return (missionTreeBean.getLevel() > 1)
                && (dataRepository.getLevelCompletionByMissionTreeId(previousMissionTreeBean.getId()) == null);
    }

    private static boolean hasUserCompletedPrerequisiteMissions(
            MissionPlaceHolder holder,
            DataRepository dataRepository) {

        for (MissionPlaceHolder child : holder.getChildren()) {
            MissionCompletionBean childCompletion =
                    dataRepository.getMissionCompletion(child.getMissionBean().getId());
            if (!childCompletion.getStudyComplete()) {
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
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            MissionPlaceHolder holder) {

        if (isLevelLocked(missionLadderId, missionTreeBean, dataRepository)) {
            return LockReason.LEVEL_LOCKED;
        } else if (!hasUserCompletedPrerequisiteMissions(holder, dataRepository)) {
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
            MissionPlaceHolder holder,
            Long missionLadderId,
            MissionTreeBean missionTreeBean) {

        LockReason lockReason = determineLockReason(
                dataRepository,
                missionLadderId,
                missionTreeBean,
                holder);

        switch (lockReason) {
            case LEVEL_LOCKED:
                return context.getString(R.string.locked_level_mission_lock_reason);
            case MISSING_MISSION:
                return context.getString(R.string.prerequisites_mission_lock_reason);
            case MISSING_POINTS:
                return context.getString(R.string.missing_points_lock_reason);
            default:
                throw new RuntimeException("Unexpected lock reason: " + lockReason);
        }
    }
}
