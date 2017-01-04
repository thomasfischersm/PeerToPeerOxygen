package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Space;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class that lays out all the missions into a pretty visual tree structure.
 */
public class MissionTreeBuilder {

    private static final String LOG_CAT = MissionTreeBuilder.class.getSimpleName();

    private final int maxColumn;

    private MissionWrapper bossWrapper;
    private Set<MissionWrapper> wrappers;
    private Set<MissionWrapper> bossTreeWrappers;
    private Map<Long, MissionWrapper> missionIdToWrapperMap;
    private Map<Integer, List<MissionWrapper>> ordinalToWrapperMap;
    private Set<OrphanTree> orphanTrees;
    private Map<Integer, List<OrphanTree>> sizeComplexityToOrphanTreeMap;
    private MissionGrid missionGrid;

    public MissionTreeBuilder(
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            int maxColumn,
            DataRepository dataRepository) {

        this.maxColumn = maxColumn;

        initMissionWrapper(missionLadderId, missionTreeBean, dataRepository);
//        fixIsConnectedToBossMission(bossWrapper);
        findBossTree();
        organizeWrappersByOrdinal();
        findOrphanTrees();
        organizeOrphanTreesBySizeComplexity();
        placeBossTree();
        placeOrphanTrees();
    }

    Set<MissionWrapper> initMissionWrapper(
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            DataRepository dataRepository) {

        List<MissionBean> missionBeans = missionTreeBean.getMissionBeans();
        if (missionBeans == null) {
            missionBeans = new ArrayList<>();
        }

        wrappers = new HashSet<>(missionBeans.size());
        missionIdToWrapperMap = new HashMap<>(missionBeans.size());

        for (MissionBean missionBean : missionBeans) {
            boolean isBossMission = missionTreeBean.getBossMissionId().equals(missionBean.getId());
            MissionWrapper wrapper = new MissionWrapper(
                    missionBean,
                    isBossMission,
                    missionLadderId,
                    missionTreeBean,
                    dataRepository);
            wrappers.add(wrapper);
            missionIdToWrapperMap.put(missionBean.getId(), wrapper);

            if (isBossMission) {
                bossWrapper = wrapper;
            }
        }

        for (MissionWrapper wrapper : wrappers) {
            wrapper.init(missionIdToWrapperMap);
        }

        return wrappers;
    }

    /**
     * Fixes a flaw in {@link MissionWrapper#getConnectedToBossMission()}.
     *
     * <p>If there is a mission path with a mission A that leads to the boss mission, mission A
     * can have a parent B that does not lead to the boss mission. That mission B can have a child
     * mission C. The recursive code in {@link MissionWrapper} would miss marking that mission as
     * connected to the boss misison.
     */
//    static void fixIsConnectedToBossMission(MissionWrapper bossWrapper) {
//        if (bossWrapper == null) {
//            return;
//        }
//
//        Set<MissionWrapper> fixedWrappers = new HashSet<>();
//        Set<MissionWrapper> pendingWrappers = new HashSet<>();
//        pendingWrappers.add(bossWrapper);
//        while (pendingWrappers.size() > 0) {
//            MissionWrapper wrapper = pendingWrappers.iterator().next();
//            wrapper.setConnectedToBossMission(true);
//            pendingWrappers.remove(wrapper);
//            fixedWrappers.add(wrapper);
//
//            for (MissionWrapper parent : wrapper.getParents()) {
//                if (!fixedWrappers.contains(parent)) {
//                    pendingWrappers.add(parent);
//                }
//            }
//
//            for (MissionWrapper child : wrapper.getChildren()) {
//                if (!fixedWrappers.contains(child)) {
//                    pendingWrappers.add(child);
//                }
//            }
//        }
//    }

    private void findBossTree() {
        bossTreeWrappers = new HashSet<>();
        Iterator<MissionWrapper> wrappersIterator = wrappers.iterator();
        while (wrappersIterator.hasNext()) {
            MissionWrapper wrapper = wrappersIterator.next();
            if (wrapper.getConnectedToBossMission()) {
                bossTreeWrappers.add(wrapper);
//                wrappersIterator.remove();
            }
        }
    }

    private void organizeWrappersByOrdinal() {
        ordinalToWrapperMap = MissionTreeBuilderUtil.organizeByOrdinal(bossTreeWrappers);
    }

    private void findOrphanTrees() {
        orphanTrees = new HashSet<>();

        // Find orphan missions.
        Set<MissionWrapper> orphanWrappers = new HashSet<>();
        for (MissionWrapper wrapper : wrappers) {
            if (!wrapper.getConnectedToBossMission()) {
                orphanWrappers.add(wrapper);
            }
        }

        // Keep making orphan trees until all orphan missions are in an orphan tree.
        orphanTrees = new HashSet<>();
        while (orphanWrappers.size() > 0) {
            MissionWrapper wrapper = orphanWrappers.iterator().next();
            orphanTrees.add(new OrphanTree(wrapper, orphanWrappers));
        }
    }

    private void organizeOrphanTreesBySizeComplexity() {
        sizeComplexityToOrphanTreeMap = new HashMap<>();

        for (OrphanTree orphanTree : orphanTrees) {
            int sizeComplexity = orphanTree.getSizeComplexity();
            if (!sizeComplexityToOrphanTreeMap.containsKey(sizeComplexity)) {
                sizeComplexityToOrphanTreeMap.put(sizeComplexity, new ArrayList<OrphanTree>());
            }
            sizeComplexityToOrphanTreeMap.get(sizeComplexity).add(orphanTree);
        }
    }

    private void placeBossTree() {
        missionGrid = new MissionGrid(maxColumn);

        // Place boss mission on top.
        int middleColumn = maxColumn / 2;
        missionGrid.add(0, middleColumn, bossWrapper);

        // Place the missions connected to the boss mission.
        List<Integer> ordinals = MissionTreeBuilderUtil.getSortedOrdinals(ordinalToWrapperMap);
        int row = 1;
        for (int ordinal : ordinals) {
            List<MissionWrapper> wrappers = ordinalToWrapperMap.get(ordinal);
            if ((ordinal == 0) && (wrappers.size() == 1)) {
                // It's only the boss mission and no sibling to the boss mission.
                continue;
            }

            Collections.sort(wrappers, new AverageParentColumnComparator());
            for (MissionWrapper wrapper : wrappers) {
                if (wrapper == bossWrapper) {
                    continue;
                }
                if (!missionGrid.attemptAdd(row, wrapper.getAverageParentColumn(), wrapper)) {
                    row++;
                    missionGrid.attemptAdd(row, wrapper.getAverageParentColumn(), wrapper);
                }
            }
            row++;
        }
    }

    private void placeOrphanTrees() {
        for (OrphanTree orphanTree : orphanTrees) {
            missionGrid.add(orphanTree);
        }
    }

    public GridLayout populateGridLayout(Context context, GridLayout gridLayout) {
        // Initialize GridLayout.
        gridLayout.setColumnCount(maxColumn);

        // Place the missions as buttons inside the grid layout.
        for (int row = 0; row < missionGrid.getMaxRow(); row++) {
            for (int column = 0; column < missionGrid.getMaxColumn(); column++) {
                MissionWrapper wrapper = missionGrid.get(row, column);
                if (wrapper != null) {
                    Log.i(LOG_CAT, "About to add button: " + row + ", " + column + " "
                            + wrapper.getMissionBean().getName());
                    Button button = MissionWrapperToButtonConverter.convert(context, wrapper);
                    gridLayout.addView(button);
                } else {
                    gridLayout.addView(createSpace(context, row, column));
                }
            }
        }

        return gridLayout;
    }

    private static Space createSpace(Context context, int row, int column) {
        GridLayout.Spec rowSpec = GridLayout.spec(row);
        GridLayout.Spec columnSpec = GridLayout.spec(column, 1, 1.0f);
        GridLayout.LayoutParams layoutParams =
                new GridLayout.LayoutParams(rowSpec, columnSpec);

        Space space = new Space(context);
        space.setLayoutParams(layoutParams);
        return space;
    }

    /**
     * Visible for testing only.
     */
    MissionWrapper getBossWrapper() {
        return bossWrapper;
    }

    /**
     * Visible for testing only.
     */
    Set<MissionWrapper> getBossTreeWrappers() {
        return bossTreeWrappers;
    }

    /**
     * Visible for testing only.
     */
    Set<MissionWrapper> getWrappers() {
        return wrappers;
    }

    /**
     * Visible for testing only.
     */
    Set<OrphanTree> getOrphanTrees() {
        return orphanTrees;
    }

    /**
     * Visible for testing only.
     */
    Map<Integer, List<MissionWrapper>> getOrdinalToWrapperMap() {
        return ordinalToWrapperMap;
    }

    /**
     * Visible for testing only.
     */
    MissionGrid getMissionGrid() {
        return missionGrid;
    }
}
