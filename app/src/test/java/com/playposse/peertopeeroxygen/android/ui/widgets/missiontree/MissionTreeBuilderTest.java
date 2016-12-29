package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * A unit test for {@link MissionTreeBuilder}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class MissionTreeBuilderTest {

    private static final Long MISSION_LADDER_ID = 1_234L;
    private static final Long MISSION_TREE_ID = 123L;

    private DataRepository dataRepository;
    private MissionTreeBean missionTreeBean;

    private MissionBean bossMission;
    private MissionBean childAMission;
    private MissionBean childBMission;
    private MissionBean childAAMission;
    private MissionBean childABMission;
    private MissionBean childBAMission;
    private MissionBean childBBMission;
    private MissionBean orphanMission;

    @Before
    public void setUp() {
        // Create missions.
        childBBMission = MissionTreeTestData.createTestMission(7L, "child BB");
        childBAMission = MissionTreeTestData.createTestMission(6L, "child BA");
        childABMission = MissionTreeTestData.createTestMission(5L, "child AB");
        childAAMission = MissionTreeTestData.createTestMission(4L, "child AA");
        childBMission = MissionTreeTestData.createTestMission(3L, "child B", childBAMission, childBBMission);
        childAMission = MissionTreeTestData.createTestMission(2L, "child A", childAAMission, childABMission);
        bossMission = MissionTreeTestData.createTestMission(1L, "boss", childAMission, childBMission);
        orphanMission = MissionTreeTestData.createTestMission(8L, "orphan");

        // Create mission tree.
        missionTreeBean = new MissionTreeBean();
        missionTreeBean.setId(MISSION_TREE_ID);
        missionTreeBean.setBossMissionId(bossMission.getId());
        missionTreeBean.setLevel(1);
        missionTreeBean.setMissionBeans(new ArrayList<MissionBean>(Arrays.asList(
                bossMission,
                childAMission,
                childBMission,
                childAAMission,
                childABMission,
                childBAMission,
                childBBMission,
                orphanMission)));

        // Create user inside of dataRepository.
        dataRepository = MissionTreeTestData.createDataRepository(
                false,
                MISSION_LADDER_ID,
                missionTreeBean);

        // Mock Android logging.
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void initMissionWrapper() {
        MissionTreeBuilder builder =
                new MissionTreeBuilder(null, missionTreeBean, 3, dataRepository);
        Set<MissionWrapper> wrappers =
                builder.initMissionWrapper(null, missionTreeBean, dataRepository);

        assertEquals(8, wrappers.size());
        wrappers.remove(assertWrapper(wrappers, bossMission, true));
        wrappers.remove(assertWrapper(wrappers, childAMission, false));
        wrappers.remove(assertWrapper(wrappers, childBMission, false));
        wrappers.remove(assertWrapper(wrappers, childAAMission, false));
        wrappers.remove(assertWrapper(wrappers, childABMission, false));
        wrappers.remove(assertWrapper(wrappers, childBAMission, false));
        wrappers.remove(assertWrapper(wrappers, childBBMission, false));
        wrappers.remove(assertWrapper(wrappers, orphanMission, false));
        assertEquals(0, wrappers.size());
    }

    private static MissionWrapper assertWrapper(
            Set<MissionWrapper> wrappers,
            MissionBean missionBean,
            boolean isBossMission) {

        MissionWrapper wrapper = getWrapperById(wrappers, missionBean.getId());
        assertEquals(missionBean, wrapper.getMissionBean());
        assertEquals(isBossMission, wrapper.isBossMission());
        return wrapper;
    }

    private static MissionWrapper getWrapperById(Set<MissionWrapper> wrappers, Long id) {
        for (MissionWrapper wrapper : wrappers) {
            if (id.equals(wrapper.getMissionBean().getId())) {
                return wrapper;
            }
        }
        return null;
    }
}
