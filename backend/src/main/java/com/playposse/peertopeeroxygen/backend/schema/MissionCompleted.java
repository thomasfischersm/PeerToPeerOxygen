package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Objectify Entity that keeps track of how many missions have been completed.
 */
@Entity
public class MissionCompleted {

    @Id private Long id;
    private Ref<Mission> missionRef;
    private int count;
    private List<Long> completed = new ArrayList<>();

    /**
     * Default constructor for Objectify.
     */
    public MissionCompleted() {
    }

    /**
     * Constructor for creating a new entity. The mission is implicitly marked as completed.
     */
    public MissionCompleted(Mission mission) {
        this.missionRef = Ref.create(Key.create(Mission.class, mission.getId()));
        count = 1;
        completed.add(System.currentTimeMillis());
    }

    /**
     * Marks the mission as completed. Missions can be completed multiple times.
     */
    public void incrementCompletionCount() {
        count++;
        completed.add(System.currentTimeMillis());
    }

    public List<Long> getCompleted() {
        return completed;
    }

    public int getCount() {
        return count;
    }

    public Long getId() {
        return id;
    }

    public Ref<Mission> getMissionRef() {
        return missionRef;
    }
}
