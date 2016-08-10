package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.annotation.Id;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of {@link MissionBoss} for transport across the network.
 */
public class MissionBossBean {

    private Long id;
    private String description;
    private List<String> checks = new ArrayList<>();

    public MissionBossBean() {
    }

    public MissionBossBean(MissionBoss missionBoss) {
        this.id = missionBoss.getId();
        this.description = missionBoss.getDescription();
        this.checks = missionBoss.getChecks();
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

    public List<String> getChecks() {
        return checks;
    }

    public MissionBoss toEntity() {
        return new MissionBoss(id, description, checks);
    }
}
