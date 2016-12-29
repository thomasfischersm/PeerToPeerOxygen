package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.support.annotation.NonNull;
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
import java.util.HashMap;
import java.util.Map;

import static com.playposse.peertopeeroxygen.android.missiondependencies.MissionAvailabilityChecker.MissionAvailability.LOCKED;
import static com.playposse.peertopeeroxygen.android.missiondependencies.MissionAvailabilityChecker.MissionAvailability.UNLOCKED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * A unit test for {@link MissionWrapper}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class MissionWrapperTest {

    private static final Long MISSION_LADDER_ID = 10_000L;
    private static final Long MISSION_TREE_ID = 1_000L;

    private static final Long BOSS_MISSION_ID = 1L;
    private static final Long NORMAL_MISSION_ID = 2L;
    private static final Long BOSS_CHILD_MISSION_ID = 3L;
    private static final Long BOSS_GRAND_CHILD_MISSION_ID = 4L;
    private static final Long BOSS_SIBLING_MISSION_ID = 5L;
    private static final String BOSS_MISSION_NAME = "Boss mission";
    private static final String NORMAL_MISSION_NAME = "Normal mission";
    private static final String BOSS_CHILD_MISSION_NAME = "Boss child mission";
    private static final String BOSS_GRAND_CHILD_MISSION_NAME = "Boss grand child mission";
    private static final String BOSS_SIBLING_MISSION_NAME = "Boss sibbling mission";

    private DataRepository dataRepository;
    private MissionTreeBean missionTreeBean;

    private MissionWrapper bossWrapper;
    private MissionWrapper bossChildWrapper;
    private MissionWrapper bossGrandChildWrapper;
    private MissionWrapper bossSiblingWrapper;
    private MissionWrapper normalWrapper;

    @Before
    public void setUp() {
        // Mock Android logging.
        PowerMockito.mockStatic(Log.class);

        // Create missions.
        MissionBean normalMission =
                MissionTreeTestData.createTestMission(NORMAL_MISSION_ID, NORMAL_MISSION_NAME);
        MissionBean bossGrandChildMission = MissionTreeTestData.createTestMission(
                BOSS_GRAND_CHILD_MISSION_ID,
                BOSS_GRAND_CHILD_MISSION_NAME);
        MissionBean bossChildMission = MissionTreeTestData.createTestMission(
                BOSS_CHILD_MISSION_ID,
                BOSS_CHILD_MISSION_NAME,
                bossGrandChildMission);
        MissionBean bossSiblingMission = MissionTreeTestData.createTestMission(
                BOSS_SIBLING_MISSION_ID,
                BOSS_SIBLING_MISSION_NAME,
                bossChildMission);
        MissionBean bossMission = MissionTreeTestData.createTestMission(
                BOSS_MISSION_ID,
                BOSS_MISSION_NAME,
                bossChildMission);

        // Create mission tree.
        missionTreeBean = new MissionTreeBean();
        missionTreeBean.setId(MISSION_TREE_ID);
        missionTreeBean.setBossMissionId(bossMission.getId());
        missionTreeBean.setLevel(1);
        missionTreeBean.setMissionBeans(new ArrayList<MissionBean>(Arrays.asList(
                bossMission,
                normalMission,
                bossChildMission,
                bossGrandChildMission,
                bossSiblingMission)));

        // Create DataRepository.
        dataRepository = MissionTreeTestData.createDataRepository(
                false,
                MISSION_LADDER_ID,
                missionTreeBean);

        // Create wrappers.
        bossWrapper = createWrapper(bossMission, true);
        bossSiblingWrapper = createWrapper(bossSiblingMission, false);
        bossChildWrapper = createWrapper(bossChildMission, false);
        bossGrandChildWrapper = createWrapper(bossGrandChildMission, false);
        normalWrapper = createWrapper(normalMission, false);

        // Create map.
        Map<Long, MissionWrapper> missionIdToWrapperMap = new HashMap<>();
        missionIdToWrapperMap.put(BOSS_MISSION_ID, bossWrapper);
        missionIdToWrapperMap.put(BOSS_SIBLING_MISSION_ID, bossSiblingWrapper);
        missionIdToWrapperMap.put(BOSS_CHILD_MISSION_ID, bossChildWrapper);
        missionIdToWrapperMap.put(BOSS_GRAND_CHILD_MISSION_ID, bossGrandChildWrapper);
        missionIdToWrapperMap.put(NORMAL_MISSION_ID, normalWrapper);

        // Init wrappers.
        bossWrapper.init(missionIdToWrapperMap);
        bossSiblingWrapper.init(missionIdToWrapperMap);
        bossChildWrapper.init(missionIdToWrapperMap);
        bossGrandChildWrapper.init(missionIdToWrapperMap);
        normalWrapper.init(missionIdToWrapperMap);
    }

    @NonNull
    private MissionWrapper createWrapper(MissionBean bossMission, boolean isBossMission) {
        return new MissionWrapper(
                bossMission,
                isBossMission,
                MISSION_LADDER_ID,
                missionTreeBean,
                dataRepository);
    }

    @Test
    public void testBoss() {
        assertEquals(BOSS_MISSION_ID, bossWrapper.getMissionBean().getId());
        assertEquals(BOSS_MISSION_NAME, bossWrapper.getMissionBean().getName());
        assertTrue(bossWrapper.isBossMission());
        assertEquals(0, bossWrapper.getParents().size());
        assertEquals(1, bossWrapper.getChildren().size());
        assertTrue(bossWrapper.getLeadsToBossMission());
        assertTrue(bossWrapper.getConnectedToBossMission());
        assertEquals(0, bossWrapper.getVerticalOrdinal());
        assertEquals(0.0, bossWrapper.getAverageParentColumn());
        assertEquals(LOCKED, bossWrapper.getMissionAvailability());

        // Test placing.
        assertFalse(bossWrapper.getPlaced());
        bossWrapper.place(1, 2);
        assertTrue(bossWrapper.getPlaced());
        assertEquals(1, (int) bossWrapper.getRow());
        assertEquals(2, (int) bossWrapper.getColumn());
    }

    @Test
    public void testBossChild() {
        assertEquals(BOSS_CHILD_MISSION_ID, bossChildWrapper.getMissionBean().getId());
        assertEquals(BOSS_CHILD_MISSION_NAME, bossChildWrapper.getMissionBean().getName());
        assertFalse(bossChildWrapper.isBossMission());
        assertEquals(2, bossChildWrapper.getParents().size());
        assertEquals(1, bossChildWrapper.getChildren().size());
        assertTrue(bossChildWrapper.getLeadsToBossMission());
        assertTrue(bossChildWrapper.getConnectedToBossMission());
        assertEquals(1, bossChildWrapper.getVerticalOrdinal());
        assertEquals(LOCKED, bossChildWrapper.getMissionAvailability());

        // Test placing.
        assertFalse(bossChildWrapper.getPlaced());
        bossChildWrapper.place(1, 2);
        assertTrue(bossChildWrapper.getPlaced());
        assertEquals(1, (int) bossChildWrapper.getRow());
        assertEquals(2, (int) bossChildWrapper.getColumn());

        // Test averageParentColumn.
        bossWrapper.place(0, 2);
        bossSiblingWrapper.place(0, 3);
        assertEquals(2.5, bossChildWrapper.getAverageParentColumn());
    }

    @Test
    public void testBossGrandChild() {
        assertEquals(BOSS_GRAND_CHILD_MISSION_ID, bossGrandChildWrapper.getMissionBean().getId());
        assertEquals(
                BOSS_GRAND_CHILD_MISSION_NAME,
                bossGrandChildWrapper.getMissionBean().getName());
        assertFalse(bossGrandChildWrapper.isBossMission());
        assertEquals(1, bossGrandChildWrapper.getParents().size());
        assertEquals(0, bossGrandChildWrapper.getChildren().size());
        assertTrue(bossGrandChildWrapper.getLeadsToBossMission());
        assertTrue(bossGrandChildWrapper.getConnectedToBossMission());
        assertEquals(2, bossGrandChildWrapper.getVerticalOrdinal());
        assertEquals(UNLOCKED, bossGrandChildWrapper.getMissionAvailability());

        // Test placing.
        assertFalse(bossGrandChildWrapper.getPlaced());
        bossGrandChildWrapper.place(1, 2);
        assertTrue(bossGrandChildWrapper.getPlaced());
        assertEquals(1, (int) bossGrandChildWrapper.getRow());
        assertEquals(2, (int) bossGrandChildWrapper.getColumn());

        // Test averageParentColumn.
        bossChildWrapper.place(0, 2);
        assertEquals(2.0, bossGrandChildWrapper.getAverageParentColumn());
    }

    @Test
    public void testBossSibling() {
        assertEquals(BOSS_SIBLING_MISSION_ID, bossSiblingWrapper.getMissionBean().getId());
        assertEquals(
                BOSS_SIBLING_MISSION_NAME,
                bossSiblingWrapper.getMissionBean().getName());
        assertFalse(bossSiblingWrapper.isBossMission());
        assertEquals(0, bossSiblingWrapper.getParents().size());
        assertEquals(1, bossSiblingWrapper.getChildren().size());
        assertFalse(bossSiblingWrapper.getLeadsToBossMission());
        assertTrue(bossSiblingWrapper.getConnectedToBossMission());
        assertEquals(0, bossSiblingWrapper.getVerticalOrdinal());
        assertEquals(LOCKED, bossSiblingWrapper.getMissionAvailability());

        // Test placing.
        assertFalse(bossSiblingWrapper.getPlaced());
        bossSiblingWrapper.place(1, 2);
        assertTrue(bossSiblingWrapper.getPlaced());
        assertEquals(1, (int) bossSiblingWrapper.getRow());
        assertEquals(2, (int) bossSiblingWrapper.getColumn());

        // Test averageParentColumn.
        assertEquals(0.0, bossSiblingWrapper.getAverageParentColumn());
    }

    @Test
    public void testNormal() {
        assertEquals(NORMAL_MISSION_ID, normalWrapper.getMissionBean().getId());
        assertEquals(NORMAL_MISSION_NAME, normalWrapper.getMissionBean().getName());
        assertFalse(normalWrapper.isBossMission());
        assertEquals(0, normalWrapper.getParents().size());
        assertEquals(0, normalWrapper.getChildren().size());
        assertFalse(normalWrapper.getLeadsToBossMission());
        assertFalse(normalWrapper.getConnectedToBossMission());
        assertEquals(0, normalWrapper.getVerticalOrdinal());
        assertEquals(0.0, normalWrapper.getAverageParentColumn());
        assertEquals(UNLOCKED, normalWrapper.getMissionAvailability());

        // Test placing.
        assertFalse(normalWrapper.getPlaced());
        normalWrapper.place(1, 2);
        assertTrue(normalWrapper.getPlaced());
        assertEquals(1, (int) normalWrapper.getRow());
        assertEquals(2, (int) normalWrapper.getColumn());
    }
}
