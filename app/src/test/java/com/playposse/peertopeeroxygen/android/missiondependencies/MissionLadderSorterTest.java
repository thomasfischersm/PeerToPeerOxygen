package com.playposse.peertopeeroxygen.android.missiondependencies;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

/**
 * Unit test for {@link MissionLadderSorter}.
 *
 * <p>Note: This test uses == to assert identity of test data objects.
 */
public class MissionLadderSorterTest {

    @Test
    public void sort_normalCase() {
        List<MissionLadderBean> missionLadderBeans = new ArrayList<>();

        // 1 tree, 0 missions
        MissionLadderBean missionLadderBean0 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean0);
        addTree(missionLadderBean0);

        // 5 trees, 0 missions
        MissionLadderBean missionLadderBean1 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean1);
        addTree(missionLadderBean1);
        addTree(missionLadderBean1);
        addTree(missionLadderBean1);
        addTree(missionLadderBean1);
        addTree(missionLadderBean1);

        // 2 trees, 2 missions
        MissionLadderBean missionLadderBean2 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean2);
        MissionTreeBean missionTreeBean2_0 = addTree(missionLadderBean2);
        addMission(missionTreeBean2_0);
        MissionTreeBean missionTreeBean2_1 = addTree(missionLadderBean2);
        addMission(missionTreeBean2_1);

        // 2 trees, 1 missions
        MissionLadderBean missionLadderBean3 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean3);
        addTree(missionLadderBean3);
        MissionTreeBean missionTreeBean3_1 = addTree(missionLadderBean3);
        addMission(missionTreeBean3_1);

        // 2 trees, 3 missions
        MissionLadderBean missionLadderBean4 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean4);
        MissionTreeBean missionTreeBean4_0 = addTree(missionLadderBean4);
        addMission(missionTreeBean4_0);
        MissionTreeBean missionTreeBean4_1 = addTree(missionLadderBean4);
        addMission(missionTreeBean4_1);
        addMission(missionTreeBean4_1);

        // Sort.
        List<MissionLadderBean> sorted = MissionLadderSorter.sort(missionLadderBeans);

        // Assert outcome.
        assertEquals(5, sorted.size());
        assertSame(missionLadderBean1, sorted.get(0));
        assertSame(missionLadderBean4, sorted.get(1));
        assertSame(missionLadderBean2, sorted.get(2));
        assertSame(missionLadderBean3, sorted.get(3));
        assertSame(missionLadderBean0, sorted.get(4));
    }

    @Test
    public void sort_nullCases() {
        List<MissionLadderBean> missionLadderBeans = new ArrayList<>();
        List<MissionLadderBean> sorted = MissionLadderSorter.sort(missionLadderBeans);
        assertTrue(sorted.isEmpty());

        // Add ladder with tree list == null.
        MissionLadderBean missionLadderBean0 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean0);
        sorted = MissionLadderSorter.sort(missionLadderBeans);
        assertEquals(1, sorted.size());
        assertSame(missionLadderBean0, sorted.get(0));

        // Add second ladder with tree list == null.
        MissionLadderBean missionLadderBean1 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean1);
        sorted = MissionLadderSorter.sort(missionLadderBeans);
        assertEquals(2, sorted.size());
        assertSame(missionLadderBean0, sorted.get(0));
        assertSame(missionLadderBean1, sorted.get(1));

        // Add ladder with mission list == null.
        MissionLadderBean missionLadderBean2 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean2);
        addTree(missionLadderBean2);
        sorted = MissionLadderSorter.sort(missionLadderBeans);
        assertEquals(3, sorted.size());
        assertSame(missionLadderBean2, sorted.get(0));
        assertSame(missionLadderBean0, sorted.get(1));
        assertSame(missionLadderBean1, sorted.get(2));

        // Add second ladder with mission list == null.
        MissionLadderBean missionLadderBean3 = new MissionLadderBean();
        missionLadderBeans.add(missionLadderBean3);
        addTree(missionLadderBean3);
        sorted = MissionLadderSorter.sort(missionLadderBeans);
        assertEquals(4, sorted.size());
        assertSame(missionLadderBean2, sorted.get(0));
        assertSame(missionLadderBean3, sorted.get(1));
        assertSame(missionLadderBean0, sorted.get(2));
        assertSame(missionLadderBean1, sorted.get(3));
    }

    private static MissionTreeBean addTree(MissionLadderBean missionLadderBean) {
        if (missionLadderBean.getMissionTreeBeans() == null) {
            missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
        }

        MissionTreeBean missionTreeBean = new MissionTreeBean();
        missionLadderBean.getMissionTreeBeans().add(missionTreeBean);
        return missionTreeBean;
    }

    private static MissionBean addMission(MissionTreeBean missionTreeBean) {
        if (missionTreeBean.getMissionBeans() == null) {
            missionTreeBean.setMissionBeans(new ArrayList<MissionBean>());
        }

        MissionBean missionBean = new MissionBean();
        missionTreeBean.getMissionBeans().add(missionBean);
        return missionBean;
    }
}
