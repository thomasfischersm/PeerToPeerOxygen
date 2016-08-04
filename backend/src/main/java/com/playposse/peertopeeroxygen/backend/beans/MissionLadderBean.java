package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of {@link MissionLadder} for transport across the network.
 */
public class MissionLadderBean {

    private Long id;
    private String name;
    private String description;
//    private List<MissionTree> missionTrees = new ArrayList<>();

    public MissionLadderBean(MissionLadder missionLadder) {
        this.id = missionLadder.getId();
        this.name = missionLadder.getName();
        this.description = missionLadder.getDescription();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
