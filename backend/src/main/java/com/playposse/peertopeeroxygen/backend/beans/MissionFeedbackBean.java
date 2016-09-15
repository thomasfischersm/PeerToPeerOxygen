package com.playposse.peertopeeroxygen.backend.beans;

import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;

import javax.annotation.Nullable;

/**
 * The equivalent of {@link MissionFeedback} for transport across the network.
 */
public class MissionFeedbackBean {

    private UserBean userBean;
    private MissionBean missionBean;
    private int rating;
    private long date;
    @Nullable private String comment;

    public MissionFeedbackBean() {
    }

    public MissionFeedbackBean(MissionFeedback missionFeedback) {
        userBean = new UserBean(missionFeedback.getUserRef().get());
        missionBean = new MissionBean(missionFeedback.getMissionRef().get());
        rating = missionFeedback.getRating();
        date = missionFeedback.getDate();
        comment = missionFeedback.getComment();
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public long getDate() {
        return date;
    }

    public MissionBean getMissionBean() {
        return missionBean;
    }

    public int getRating() {
        return rating;
    }

    public UserBean getUserBean() {
        return userBean;
    }
}
