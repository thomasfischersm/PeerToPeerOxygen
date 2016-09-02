package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Objectify entity to record when a user completed a level.
 */
@Entity
@Cache
public class LevelCompletion {

    @Id private Long id;
    private Ref<MissionTree> missionTreeRef;
    @Index private Long date;

    public LevelCompletion() {
    }

    public LevelCompletion(Long date, Ref<MissionTree> missionTreeRef) {
        this.date = date;
        this.missionTreeRef = missionTreeRef;
    }

    public Long getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Ref<MissionTree> getMissionTreeRef() {
        return missionTreeRef;
    }
}
