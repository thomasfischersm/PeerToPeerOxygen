package com.playposse.peertopeeroxygen.backend.beans;

import com.playposse.peertopeeroxygen.backend.schema.MissionStats;

/**
 * The equivalent of {@link MissionStats} for transport across the network.
 */
public class MissionStatsBean {

    private MissionBean missionBean;
    private int completionCount = 0;
    private int ratingTotal = 0;
    private int ratingCount = 0;

    public MissionStatsBean() {
    }

    public MissionStatsBean(MissionStats missionStats) {
        missionBean = new MissionBean(missionStats.getMissionRef().get());
        completionCount = missionStats.getCompletionCount();
        ratingTotal = missionStats.getRatingTotal();
        ratingCount = missionStats.getRatingCount();
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public MissionBean getMissionBean() {
        return missionBean;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getRatingTotal() {
        return ratingTotal;
    }
}
