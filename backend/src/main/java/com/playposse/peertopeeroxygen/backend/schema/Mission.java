package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Objectify entity that describes a mission.
 */
@Entity
public class Mission {

    @Id private Long id;
    private String name;
    private String studentInstruction;
    private String buddyInstruction;
//    private byte[] icon;

    /**
     * Default constructor for Objectify.
     */
    public Mission() {
    }

    /**
     * Default constructor to create a new mission.
     */
    public Mission(String name, String studentInstruction, String buddyInstruction) {
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
}
