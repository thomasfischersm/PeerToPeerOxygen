package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.playposse.peertopeeroxygen.backend.schema.Mission;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of {@link Mission} for transport across the network.
 */
public class MissionBean {

    private Long id;
    private String name;
    private String studentInstruction;
    private String buddyInstruction;
    private List<Long> requiredMissionIds = new ArrayList<>();

    public MissionBean() {
    }

    public MissionBean(Mission mission) {
        this.id = mission.getId();
        this.name = mission.getName();
        this.studentInstruction = mission.getStudentInstruction();
        this.buddyInstruction = mission.getBuddyInstruction();

        for (Ref<Mission> requiredMissionRef : mission.getRequiredMissions()) {
            requiredMissionIds.add(requiredMissionRef.get().getId());
        }
    }

    public String getBuddyInstruction() {
        return buddyInstruction;
    }

    public void setBuddyInstruction(String buddyInstruction) {
        this.buddyInstruction = buddyInstruction;
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

    public String getStudentInstruction() {
        return studentInstruction;
    }

    public void setStudentInstruction(String studentInstruction) {
        this.studentInstruction = studentInstruction;
    }

    public List<Long> getRequiredMissionIds() {
        return requiredMissionIds;
    }

    public Mission toEntity() {
        Mission mission = new Mission(id, name, studentInstruction, buddyInstruction);

        for (Long requiredMissionId : requiredMissionIds) {
            Key<Mission> requiredMissionKey = Key.create(Mission.class, requiredMissionId);
            mission.getRequiredMissions().add(Ref.create(requiredMissionKey));
        }

        return mission;
    }
}
