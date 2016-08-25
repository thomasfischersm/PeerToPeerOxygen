package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Objectify entity that describes a mission.
 */
@Entity
public class Mission {

    @Id private Long id;
    private String name;
    private String studentInstruction;
    private String buddyInstruction;
    @Load private List<Ref<Mission>> requiredMissions = new ArrayList<>();
    private Map<UserPoints.PointType, Integer> pointCostMap = new HashMap<>();
//    private byte[] icon;

    /**
     * Default constructor for Objectify.
     */
    public Mission() {
    }

    /**
     * Default constructor to create a new mission.
     */
    public Mission(Long id, String name, String studentInstruction, String buddyInstruction) {
        this.id = id;
        this.buddyInstruction = buddyInstruction;
        this.name = name;
        this.studentInstruction = studentInstruction;
    }

    public Long getId() {
        return id;
    }

    public String getBuddyInstruction() {
        return buddyInstruction;
    }

    public void setBuddyInstruction(String buddyInstruction) {
        this.buddyInstruction = buddyInstruction;
    }

    public String getStudentInstruction() {
        return studentInstruction;
    }

    public void setStudentInstruction(String studentInstruction) {
        this.studentInstruction = studentInstruction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ref<Mission>> getRequiredMissions() {
        return requiredMissions;
    }

    public Map<UserPoints.PointType, Integer> getPointCostMap() {
        return pointCostMap;
    }
}
