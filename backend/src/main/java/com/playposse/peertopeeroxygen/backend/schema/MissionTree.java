package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

/**
 * Objectify entity that represents a mission tree. A mission tree is a collection of missions
 * that ends with beating the mission boss. The missions have a theme. The theme can have multiple
 * levels of mission trees. Each level is represented with its own mission tree.
 */
@Entity
public class MissionTree {

    @Id private Long id;
    private String name;
    private String description;
    private int level;
    @Load private MissionBoss missionBoss;
    @Load private List<Ref<Mission>> missions = new ArrayList<>();

    /**
     * List of missions that are required to be completed before attempting to beat the mission
     * boss.
     */
    @Load private List<Ref<Mission>> requiredMissions = new ArrayList<>();

    /**
     * Default constructor for objectify.
     */
    public MissionTree() {
    }

    public MissionTree(
            Long id,
            String name,
            String description,
            int level,
            MissionBoss missionBoss) {

        this.description = description;
        this.id = id;
        this.level = level;
        this.name = name;
        this.missionBoss = missionBoss;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Ref<Mission>> getMissions() {
        return missions;
    }

    public List<Ref<Mission>> getRequiredMissions() {
        return requiredMissions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public MissionBoss getMissionBoss() {
        return missionBoss;
    }

    public void setMissionBoss(MissionBoss missionBoss) {
        this.missionBoss = missionBoss;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
