package com.playposse.peertopeeroxygen.android.missiondependencies;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A class that detect cycles in the missing dependencies.
 */
public class MissionCycleDetector {

    public static List<MissionBean> findPossibleChildren(
            MissionBean missionBean,
            MissionTreeBean missionTreeBean) {

        if (missionTreeBean.getMissionBeans() == null) {
            return new ArrayList<>(0);
        }

        return findPossibleChildren(missionBean, makeMap(missionTreeBean.getMissionBeans()));
    }

    public static List<MissionBean> findPossibleChildren(
            MissionBean missionBean,
            Map<Long, MissionBean> missionBeanMap) {

        List<MissionBean> allMissionBeans = new ArrayList<>(missionBeanMap.values());

        // Remove self.
        allMissionBeans.remove(missionBean);

        // Remove parents.
        Set<Long> parentMissionIds = new HashSet<>();
        parentMissionIds.add(missionBean.getId());
        boolean dirty = true;
        while (dirty) {
            dirty = false;
            for (int i = 0; i < allMissionBeans.size(); i++) {
                MissionBean otherMissionBean = allMissionBeans.get(i);
                if (otherMissionBean.getRequiredMissionIds() != null) {
                    for (Long childId : otherMissionBean.getRequiredMissionIds()) {
                        if (parentMissionIds.contains(childId)) {
                            boolean isRemoved = allMissionBeans.remove(otherMissionBean);
                            parentMissionIds.add(otherMissionBean.getId());
                            dirty = dirty || isRemoved;
                        }
                    }
                }
            }
        }

        return allMissionBeans;
    }

    public static Map<Long, MissionBean> makeMap(@Nullable List<MissionBean> missionBeans) {
        if (missionBeans == null) {
            return new HashMap<>(0);
        }

        Map<Long, MissionBean> missionBeanMap = new HashMap<>(missionBeans.size());
        for (MissionBean missionBean : missionBeans) {
            missionBeanMap.put(missionBean.getId(), missionBean);
        }
        return missionBeanMap;
    }
}
