package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.JsonMap;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains local data and provides helper methods around it.
 */
public class DataRepository {

    private static final String LOG_CAT = DataRepository.class.getSimpleName();

    private CompleteMissionDataBean completeMissionDataBean;
    private FacebookProfilePhotoCache facebookProfilePhotoCache;

    public void onStart(Context context) {
        facebookProfilePhotoCache = new FacebookProfilePhotoCache();
        facebookProfilePhotoCache.onStart(context);
    }

    public void onStop(Context context) {
        facebookProfilePhotoCache.onStop(context);
        facebookProfilePhotoCache = null;
    }

    public CompleteMissionDataBean getCompleteMissionDataBean() {
        return completeMissionDataBean;
    }

    public void setCompleteMissionDataBean(CompleteMissionDataBean completeMissionDataBean) {
        this.completeMissionDataBean = completeMissionDataBean;
    }

    public FacebookProfilePhotoCache getFacebookProfilePhotoCache() {
        return facebookProfilePhotoCache;
    }

    public UserBean getUserBean() {
        if (completeMissionDataBean != null) {
            return completeMissionDataBean.getUserBean();
        } else {
            return null;
        }
    }

    public List<MissionLadderBean> getMissionLadderBeans() {
        return completeMissionDataBean.getMissionLadderBeans();
    }

    public MissionLadderBean getMissionLadderBean(Long id) {
        for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
            if (missionLadderBean.getId().equals(id)) {
                return missionLadderBean;
            }
        }
        return null;
    }

    public MissionTreeBean getMissionTreeBean(Long missionLadderId, Long missionTreeId) {
        for (MissionTreeBean missionTreeBean : getMissionLadderBean(missionLadderId).getMissionTreeBeans()) {
            if (missionTreeBean.getId().equals(missionTreeId)) {
                return missionTreeBean;
            }
        }
        return null;
    }

    public MissionBean getMissionBean(
            Long missionLadderId,
            Long missionTreeId,
            Long missionId) {

        for (MissionBean missionBean : getMissionTreeBean(missionLadderId, missionTreeId).getMissionBeans()) {
            if (missionBean.getId().equals(missionId)) {
                return missionBean;
            }
        }
        return null;
    }

    public MissionCompletionBean getMissionCompletion(Long missionId) {
        if (getUserBean().getMissionCompletionBeans() != null) {
            for (MissionCompletionBean completionBean : getUserBean().getMissionCompletionBeans()) {
                if (completionBean.getMissionId().equals(missionId)) {
                    return completionBean;
                }
            }
        } else {
            getUserBean().setMissionCompletionBeans(new ArrayList<MissionCompletionBean>());
        }

        // Create a new one.
        MissionCompletionBean completionBean = new MissionCompletionBean();
        completionBean.setMissionId(missionId);
        completionBean.setStudyCount(0);
        completionBean.setMentorCount(0);
        completionBean.setStudyComplete(false);
        completionBean.setMentorCheckoutComplete(false);
        getUserBean().getMissionCompletionBeans().add(completionBean);
        return completionBean;
    }

    /**
     * Finds the mission ladder id and mission tree id.
     *
     * @return Long[] An array with the ids for the mission ladder, mission tree, and mission.
     */
    public Long[] getMissionPath(Long missionId) {
        for (MissionLadderBean ladderBean : completeMissionDataBean.getMissionLadderBeans()) {
            for (MissionTreeBean treeBean : ladderBean.getMissionTreeBeans()) {
                for (MissionBean missionBean : treeBean.getMissionBeans()) {
                    if (missionId.equals(missionBean.getId())) {
                        return new Long[]{ladderBean.getId(), treeBean.getId(), missionId};
                    }
                }
            }
        }

        Log.e(LOG_CAT, "Couldn't find mission " + missionId);
        return null;
    }

    public static int getPointByType(UserBean userBean, PointType pointType) {
        return getPointByType(userBean.getPointsMap(), pointType);
    }

    public static int getPointByType(MissionBean missionBean, PointType pointType) {
        return getPointByType(missionBean.getPointCostMap(), pointType);
    }

    private static int getPointByType(JsonMap pointsMap, PointType pointType) {
        if ((pointsMap != null) && pointsMap.containsKey(pointType.name())) {
            Object obj = pointsMap.get(pointType.name());
            return Integer.parseInt(obj.toString());
        }
        return 0;
    }

    public static void setPoint(MissionBean missionBean, int pointCount, PointType pointType) {
        if (missionBean.getPointCostMap() == null) {
            missionBean.setPointCostMap(new JsonMap());
        }

        missionBean.getPointCostMap().put(pointType.name(), pointCount);
    }

    public static void addPoints(UserBean userBean, PointType pointType, int pointCount) {
        JsonMap pointsMap = userBean.getPointsMap();
        if (pointsMap == null) {
            userBean.setPointsMap(new JsonMap());
            pointsMap = userBean.getPointsMap();
        }

        if (pointsMap.containsKey(pointType.name())) {
            Object obj = pointsMap.get(pointType.name());
            pointCount += Integer.parseInt(obj.toString());
        }

        pointsMap.put(pointType.name(), pointCount);
    }
}
