package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of {@link MissionTree} for transport across the network.
 */
public class MissionTreeBean {

    private Long id;
    private String name;
    private String description;
    private int level;
//    private MissionBoss missionBoss;
//    private List<Mission> missions = new ArrayList<>();

    public MissionTreeBean() {
    }

    public MissionTreeBean(MissionTree missionTree) {
        this.id = missionTree.getId();
        this.name = missionTree.getName();
        this.description = missionTree.getDescription();
        this.level = missionTree.getLevel();
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MissionTree toEntity() {
        return new MissionTree(id, name, description, level);
    }
}
