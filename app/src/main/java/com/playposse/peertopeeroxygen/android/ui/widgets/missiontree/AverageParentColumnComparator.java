package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import java.util.Comparator;

/**
 * A {@link Comparator} that sorts {@link MissionWrapper} by {@code averageParentColumn}
 */
public class AverageParentColumnComparator implements Comparator<MissionWrapper> {

    @Override
    public int compare(MissionWrapper wrapper0, MissionWrapper wrapper1) {
        Double avg0 = (Double) wrapper0.getAverageParentColumn();
        double avg1 = wrapper1.getAverageParentColumn();
        return avg0.compareTo(avg1);
    }
}
