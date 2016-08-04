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
    @Load private Ref<MissionBoss> missionBoss;
    @Load private List<Mission> missions = new ArrayList<>();

    /**
     * List of missions that are required to be completed before attempting to beat the mission
     * boss.
     */
    @Load private List<Mission> requiredMissions = new ArrayList<>();

    /**
     * Default constructor for objectify.
     */
    public MissionTree() {
    }


    /**
     * Constructor for creating a new mission tree.
     */
    public MissionTree(String description, int level, String name) {
        this.description = description;
        this.level = level;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public List<Mission> getRequiredMissions() {
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

    public Ref<MissionBoss> getMissionBoss() {
        return missionBoss;
    }

    public void setMissionBoss(Ref<MissionBoss> missionBoss) {
        this.missionBoss = missionBoss;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
