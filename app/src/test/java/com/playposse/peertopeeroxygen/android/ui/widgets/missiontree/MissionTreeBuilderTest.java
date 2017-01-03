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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * A unit test for {@link MissionTreeBuilder}.
 *
 * <p>This test expects a tree that is laid out as follows:
 * <code>
 *     ----------------------------------
 *     |          | Boss     |          |
 *     ----------------------------------
 *     | Child A  | Child B  | Orphan   |
 *     ----------------------------------
 *     | Child AA | Child AB | Child BA |
 *     ----------------------------------
 *     |          | Child BB |          |
 *     ----------------------------------
 * </code>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class MissionTreeBuilderTest {

    private static final int MAX_COLUMNS = 3;
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
        missionTreeBean.setMissionBeans(new ArrayList<>(Arrays.asList(
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
                new MissionTreeBuilder(null, missionTreeBean, MAX_COLUMNS, dataRepository);
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
        assertNotNull(wrapper);
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

    @Test
    public void findBossTree() {
        MissionTreeBuilder builder =
                new MissionTreeBuilder(null, missionTreeBean, 3, dataRepository);

        assertSame(bossMission, builder.getBossWrapper().getMissionBean());

        Set<MissionWrapper> bossTreeWrappers = builder.getBossTreeWrappers();
        assertEquals(7, bossTreeWrappers.size());
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, bossMission, true));
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, childAMission, false));
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, childBMission, false));
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, childAAMission, false));
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, childABMission, false));
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, childBAMission, false));
        bossTreeWrappers.remove(assertWrapper(bossTreeWrappers, childBBMission, false));
        assertEquals(0, bossTreeWrappers.size());
    }

    @Test
    public void organizeWrappersByOrdinal() {
        MissionTreeBuilder builder =
                new MissionTreeBuilder(null, missionTreeBean, MAX_COLUMNS, dataRepository);
        Map<Integer, List<MissionWrapper>> ordinalToWrapperMap = builder.getOrdinalToWrapperMap();

        assertEquals(3, ordinalToWrapperMap.size());

        List<MissionWrapper> ordinal0Wrappers = ordinalToWrapperMap.get(0);
        assertEquals(1, ordinal0Wrappers.size());
        assertWrapper(new HashSet<>(ordinal0Wrappers), bossMission, true);

        List<MissionWrapper> ordinal1Wrappers = ordinalToWrapperMap.get(1);
        assertEquals(2, ordinal1Wrappers.size());
        assertWrapper(new HashSet<>(ordinal1Wrappers), childAMission, false);
        assertWrapper(new HashSet<>(ordinal1Wrappers), childBMission, false);

        List<MissionWrapper> ordinal2Wrappers = ordinalToWrapperMap.get(2);
        assertEquals(4, ordinal2Wrappers.size());
        assertWrapper(new HashSet<>(ordinal2Wrappers), childAAMission, false);
        assertWrapper(new HashSet<>(ordinal2Wrappers), childABMission, false);
        assertWrapper(new HashSet<>(ordinal2Wrappers), childBAMission, false);
        assertWrapper(new HashSet<>(ordinal2Wrappers), childBBMission, false);
    }

    @Test
    public void findOrphanTrees() {
        MissionTreeBuilder builder =
                new MissionTreeBuilder(null, missionTreeBean, MAX_COLUMNS, dataRepository);

        Set<OrphanTree> orphanTrees = builder.getOrphanTrees();
        assertEquals(1, orphanTrees.size());

        OrphanTree orphanTree = orphanTrees.iterator().next();
        assertEquals(1, orphanTree.getSizeComplexity());
        assertThat(orphanTree.getSortedOrdinals(), is(Arrays.asList(0)));
    }

    @Test
    public void placeBossTree() {
        MissionTreeBuilder builder =
                new MissionTreeBuilder(null, missionTreeBean, MAX_COLUMNS, dataRepository);
        MissionGrid missionGrid = builder.getMissionGrid();

        assertEquals(MAX_COLUMNS, missionGrid.getMaxColumn());
        assertEquals(4, missionGrid.getMaxRow());

        // first row
        assertNull(missionGrid.get(0, 0));
        assertMissionInGrid(missionGrid, bossMission, 0, 1);
        assertNull(missionGrid.get(0, 2));

        // second row
        assertMissionInGrid(missionGrid, childAMission, 1, 0);
        assertMissionInGrid(missionGrid, childBMission, 1, 1);
        assertMissionInGrid(missionGrid, orphanMission, 1, 2);

        // third row
        assertMissionInGrid(missionGrid, childAAMission, 2, 0);
        assertMissionInGrid(missionGrid, childABMission, 2, 1);
        assertMissionInGrid(missionGrid, childBAMission, 2, 2);

        // fourth row
        assertNull(missionGrid.get(0, 0));
        assertMissionInGrid(missionGrid, childBBMission, 3, 1);
        assertNull(missionGrid.get(0, 2));
    }

    private void assertMissionInGrid(
            MissionGrid missionGrid,
            MissionBean missionBean,
            int row,
            int column) {

        MissionWrapper wrapper = missionGrid.get(row, column);
        assertSame(missionBean, wrapper.getMissionBean());
        assertEquals(Integer.valueOf(row), wrapper.getRow());
        assertEquals(Integer.valueOf(column), wrapper.getColumn());
    }
}
