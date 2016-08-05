package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Objectify Entity that models missions ladders. A rung of mission levels has a type, e.g.
 * technique missions. The {@link MissionLadder} represents the list of {@link MissionTree}s.
 *
 * <p>1 Mission ladder has multiple levels (mission tree). The mission tree are the individual
 * missions that have to be completed order by their prerequisites.
 */
@Entity
public class MissionLadder {

    @Id private Long id;
    private String name;
    private String description;
    @Load private List<MissionTree> missionTrees = new ArrayList<>();

//    private byte[] icon;

    /**
     * Default constructor for Objectify.
     */
    public MissionLadder() {
    }

    /**
     * Constructor for creating a new mission ladder.
     */
    public MissionLadder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor for converting bean to entity.
     */
    public MissionLadder(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Sorts the {@link MissionTree}s when the entity is loaded by level.
     */
    @OnLoad
    public void onLoad() {
        Collections.sort(missionTrees, new MissionTreeComparator());
    }

    public List<MissionTree> getMissionTrees() {
        return missionTrees;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static final class MissionTreeComparator implements Comparator<MissionTree> {

        @Override
        public int compare(MissionTree missionTree0, MissionTree missionTree1) {
//            int level0 = missionTree0.getLevel();
//            int level1 = missionTree1.getLevel();
            return missionTree0.getLevel() - missionTree1.getLevel();
//            return (level0 < level1 ? -1 : (level0==level1 ? 0 : 1));
        }
    }
}
