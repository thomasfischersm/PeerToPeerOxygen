package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
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
    @Load private List<Ref<MissionTree>> missionTreeRefs = new ArrayList<>();

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
        Collections.sort(missionTreeRefs, new MissionTreeRefComparator());
    }

    public List<Ref<MissionTree>> getMissionTreeRefs() {
        return missionTreeRefs;
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

    private static final class MissionTreeRefComparator implements Comparator<Ref<MissionTree>> {

        @Override
        public int compare(Ref<MissionTree> missionTree0, Ref<MissionTree> missionTree1) {
            return missionTree0.getValue().getLevel() - missionTree1.getValue().getLevel();
        }
    }
}
