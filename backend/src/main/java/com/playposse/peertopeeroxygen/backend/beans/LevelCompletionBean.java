package com.playposse.peertopeeroxygen.backend.beans;

import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;

/**
 * An end point bean that contains a level that has been completed.
 */
public class LevelCompletionBean {

    private Long id;
    private Long missionTreeId;

    public LevelCompletionBean() {
    }

    public LevelCompletionBean(LevelCompletion levelCompletion) {
        id = levelCompletion.getId();
        missionTreeId = levelCompletion.getMissionTreeRef().getKey().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMissionTreeId() {
        return missionTreeId;
    }

    public void setMissionTreeId(Long missionTreeId) {
        this.missionTreeId = missionTreeId;
    }
}
