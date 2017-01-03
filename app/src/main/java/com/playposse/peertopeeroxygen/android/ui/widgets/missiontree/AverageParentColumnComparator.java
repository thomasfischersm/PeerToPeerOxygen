package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import java.util.Comparator;

/**
 * A {@link Comparator} that sorts {@link MissionWrapper} by {@code averageParentColumn}
 *
 * <p>For tie breaks, use alphanumeric comparison of the mission name.
 */
public class AverageParentColumnComparator implements Comparator<MissionWrapper> {

    @Override
    public int compare(MissionWrapper wrapper0, MissionWrapper wrapper1) {
        Double avg0 = (Double) wrapper0.getAverageParentColumn();
        double avg1 = wrapper1.getAverageParentColumn();
        int comparison = avg0.compareTo(avg1);

        if (comparison != 0) {
            return comparison;
        } else {
            String name0 = wrapper0.getMissionBean().getName();
            String name1 = wrapper1.getMissionBean().getName();
            return name0.compareTo(name1);
        }
    }
}
