package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods for laying out the mission tree.
 */
public class MissionTreeBuilderUtil {

    public static Map<Integer, List<MissionWrapper>> organizeByOrdinal(
            Set<MissionWrapper> wrappers) {

        Map<Integer, List<MissionWrapper>> ordinalToWrapperMap = new HashMap<>();
        for (MissionWrapper wrapper : wrappers) {
            if (ordinalToWrapperMap.containsKey(wrapper.getVerticalOrdinal())) {
                ordinalToWrapperMap.get(wrapper.getVerticalOrdinal()).add(wrapper);
            } else {
                List<MissionWrapper> wrapperList = new ArrayList<>();
                wrapperList.add(wrapper);
                ordinalToWrapperMap.put(wrapper.getVerticalOrdinal(), wrapperList);
            }
        }

        return ordinalToWrapperMap;
    }

    public static List<Integer> getSortedOrdinals(
            Map<Integer, List<MissionWrapper>> ordinalToWrapperMap) {
        List<Integer> ordinals = new ArrayList<>(ordinalToWrapperMap.keySet());
        Collections.sort(ordinals);
        return ordinals;
    }
}
