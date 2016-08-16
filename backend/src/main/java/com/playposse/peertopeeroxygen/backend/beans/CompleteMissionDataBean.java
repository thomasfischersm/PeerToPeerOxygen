package com.playposse.peertopeeroxygen.backend.beans;

import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;

import java.util.ArrayList;
import java.util.List;

/**
 * A bean for the end point to transmit all the mission data (ladders, trees, and individual
 * missions).
 */
public class CompleteMissionDataBean {

    private final List<MissionLadderBean> missionLadderBeans = new ArrayList<>();
    private UserBean userBean;

    public CompleteMissionDataBean(UserBean userBean, List<MissionLadder> missionLadders) {
        this.userBean = userBean;

        for (MissionLadder missionLadder : missionLadders) {
            missionLadderBeans.add(new MissionLadderBean(missionLadder));
        }
    }

    public List<MissionLadderBean> getMissionLadderBeans() {
        return missionLadderBeans;
    }
}