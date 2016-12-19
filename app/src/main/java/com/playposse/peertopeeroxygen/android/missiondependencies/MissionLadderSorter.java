package com.playposse.peertopeeroxygen.android.missiondependencies;

import android.support.annotation.NonNull;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Takes a list of {@link MissionLadderBean}s and sorts them by the "biggest" one first. Big is
 * defined as having the most {@link MissionTreeBean}s. If the number is equal, the total amount
 * of {@link MissionBean}s is counted.
 *
 * <p>The assumption is that if something has more levels, it is a more involved path even though,
 * it may have less missions.
 */
public class MissionLadderSorter {

    public static List<MissionLadderBean> sort(List<MissionLadderBean> missionLadderBeans) {
        List<Wrapper> wrappers = new ArrayList<>(missionLadderBeans.size());
        for (MissionLadderBean missionLadderBean : missionLadderBeans) {
            wrappers.add(new Wrapper(missionLadderBean));
        }

        Collections.sort(wrappers);

        List<MissionLadderBean> result = new ArrayList<>(missionLadderBeans.size());
        for (Wrapper wrapper : wrappers) {
            result.add(wrapper.getMissionLadderBean());
        }
        return result;
    }

    private static Integer countMissionTrees(MissionLadderBean missionLadderBean) {
        if (missionLadderBean.getMissionTreeBeans() == null) {
            return 0;
        } else {
            return missionLadderBean.getMissionTreeBeans().size();
        }
    }

    private static Integer countMissions(MissionLadderBean missionLadderBean) {
        if (missionLadderBean.getMissionTreeBeans() == null) {
            return 0;
        }

        int missionTotal = 0;
        for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
            if (missionTreeBean.getMissionBeans() != null) {
                missionTotal += missionTreeBean.getMissionBeans().size();
            }
        }

        return missionTotal;
    }

    /**
     * A class to encapsulate the {@link MissionLadderBean} with the cached mission tree and mission
     * counts.
     */
    private static final class Wrapper implements Comparable<Wrapper> {

        private final MissionLadderBean missionLadderBean;

        private Integer missionTreeCount = null;
        private Integer missionCount = null;

        private Wrapper(MissionLadderBean missionLadderBean) {
            this.missionLadderBean = missionLadderBean;
        }

        @Override
        public int compareTo(@NonNull Wrapper other) {
            // Calculate missionTreeCounts if necessary.
            if (missionTreeCount == null) {
                missionTreeCount = countMissionTrees(missionLadderBean);
            }

            if (other.missionTreeCount == null) {
                other.missionTreeCount = countMissionTrees(other.missionLadderBean);
            }

            // Compare levels.
            int missionTreeComparison = missionTreeCount.compareTo(other.missionTreeCount);
            if (missionTreeComparison != 0) {
                return -missionTreeComparison;
            }

            // Calculate missionCounts if necessary.
            if (missionCount == null) {
                missionCount = countMissions(missionLadderBean);
            }

            if (other.missionCount == null) {
                other.missionCount = countMissions(other.missionLadderBean);
            }

            return -missionCount.compareTo(other.missionCount);
        }

        public MissionLadderBean getMissionLadderBean() {
            return missionLadderBean;
        }
    }
}
