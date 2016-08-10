package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Objectify entity that describes the final test to pass a level.
 */
@Entity
public class MissionBoss {

    @Id private Long id;
    private String description;
    private List<String> checks = new ArrayList<>();

    /**
     * Default constructor for Objectify.
     */
    public MissionBoss() {
    }

    /**
     * Default constructor to create a new mission boss.
     */
    public MissionBoss(Long id, String description, List<String> checks) {
        this.id = id;
        this.checks = checks;
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<String> getChecks() {
        return checks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
