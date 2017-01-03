package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of an orphan tree. An orphan tree are missions that are connected together yet
 * aren't connected to the boss mission.
 */
public class OrphanTree {

    private static final String LOG_CAT = OrphanTree.class.getSimpleName();

    private final Set<MissionWrapper> wrappers;
    private final Map<Integer, List<MissionWrapper>> ordinalToWrapperMap;
    private final List<Integer> sortedOrdinals;

    private Integer sizeComplexity = null;

    /**
     * Builds an orphan tree.
     *
     * @param startWrapper      A {@link MissionWrapper} that refers to a random mission in the tree.
     * @param availableWrappers A list of missions that aren't attached to the main tree or other
     *                          orphan trees yet.
     */
    public OrphanTree(MissionWrapper startWrapper, Set<MissionWrapper> availableWrappers) {
        wrappers = findTreeMembers(startWrapper, availableWrappers);
        ordinalToWrapperMap = MissionTreeBuilderUtil.organizeByOrdinal(wrappers);
        sortedOrdinals = MissionTreeBuilderUtil.getSortedOrdinals(ordinalToWrapperMap);
    }

    private static Set<MissionWrapper> findTreeMembers(
            MissionWrapper startWrapper,
            Set<MissionWrapper> availableWrappers) {

        Set<MissionWrapper> wrappers = new HashSet<>();
        List<MissionWrapper> wrapperStack = new ArrayList<>();
        wrapperStack.add(startWrapper);

        Log.i(LOG_CAT, "Finding members for orphan tree.");
        while (!wrapperStack.isEmpty()) {
            MissionWrapper wrapper = wrapperStack.remove(0);
            Log.i(LOG_CAT, "Considering adding mission to orphan tree: "
                    + wrapper.getMissionBean().getId() + " "
                    + wrapper.getMissionBean().getName());

//            if (!availableWrappers.contains(wrapper) && !wrappers.contains(wrapper)) {
//                throw new RuntimeException(
//                        "The mission is already attached to another orphan tree: "
//                                + wrapper.getMissionBean().getId() + " "
//                                + wrapper.getMissionBean().getName());
//            }

            if (wrappers.contains(wrapper)) {
                // The mission has already been referenced by another reference and has been added.
                continue;
            }

            wrappers.add(wrapper);
            availableWrappers.remove(wrapper);
            wrapperStack.addAll(wrapper.getParents());
            wrapperStack.addAll(wrapper.getChildren());
            Log.i(LOG_CAT, "Added mission to orphan tree: "
                    + wrapper.getMissionBean().getId() + " "
                    + wrapper.getMissionBean().getName());
        }
        return wrappers;
    }

    public int getSizeComplexity() {
        if (sizeComplexity == null) {
            int height = 1;
            int width = 1;

            for (Integer ordinal : ordinalToWrapperMap.keySet()) {
                height = Math.max(height, ordinal);
                width = Math.max(width, ordinalToWrapperMap.get(ordinal).size());
            }

            sizeComplexity = height * width;
        }
        return sizeComplexity;
    }

    public Map<Integer, List<MissionWrapper>> getOrdinalToWrapperMap() {
        return ordinalToWrapperMap;
    }

    public List<Integer> getSortedOrdinals() {
        return sortedOrdinals;
    }
}
