package com.playposse.peertopeeroxygen.android.missiondependencies;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link MissionCycleDetector}.
 */

public class MissionCycleDetectorTest {

    @Test
    public void findPossibleChildren() {
        List<MissionBean> missionBeans = new ArrayList<>();
        missionBeans.add(createMissionBean(1, 2, 5));
        missionBeans.add(createMissionBean(2, 3, 4));
        missionBeans.add(createMissionBean(3));
        missionBeans.add(createMissionBean(4));
        missionBeans.add(createMissionBean(5));
        missionBeans.add(createMissionBean(6));

        Map<Long, MissionBean> missionBeanMap = MissionCycleDetector.makeMap(missionBeans);
        assertMissionBeanList(missionBeanMap, 1, 2, 3, 4, 5, 6);
        assertMissionBeanList(missionBeanMap, 2, 3, 4, 5, 6);
        assertMissionBeanList(missionBeanMap, 3, 4, 5, 6);
        assertMissionBeanList(missionBeanMap, 4, 3, 5, 6);
        assertMissionBeanList(missionBeanMap, 5, 2, 3, 4, 6);
        assertMissionBeanList(missionBeanMap, 6, 1, 2, 3, 4, 5);
    }

    private static MissionBean createMissionBean(long missionBeanId, long... childMissionBeanIds) {
        MissionBean missionBean = new MissionBean();
        missionBean.setRequiredMissionIds(new ArrayList<Long>());
        missionBean.setId(missionBeanId);
        for (Long childId : childMissionBeanIds) {
            missionBean.getRequiredMissionIds().add(childId);
        }
        return missionBean;
    }

    private static void assertMissionBeanList(
            Map<Long, MissionBean> missionBeanMap,
            long targetMissionId,
            long... missionIds) {

        MissionBean targetMissionBean = missionBeanMap.get(targetMissionId);
        List<MissionBean> possibleChildren =
                MissionCycleDetector.findPossibleChildren(targetMissionBean, missionBeanMap);

        assertEquals(
                "The mission list had an unexpected size: " + toIdString(possibleChildren),
                missionIds.length,
                possibleChildren.size());

        for (Long missionId : missionIds) {
            for (MissionBean missionBean : possibleChildren) {
                if (missionBean.getId().equals(missionId)) {
                    possibleChildren.remove(missionBean);
                    break;
                }
            }
        }

        assertEquals(0, possibleChildren.size());
    }

    private static String toIdString(List<MissionBean> missionBeanList) {
        StringBuilder sb = new StringBuilder();
        for (MissionBean missionBean : missionBeanList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(missionBean.getId());
        }
        return sb.toString();
    }
}
