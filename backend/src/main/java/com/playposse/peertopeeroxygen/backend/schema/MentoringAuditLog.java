package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import javax.annotation.Nullable;

/**
 * Objectify entity that represents one teaching event for the purpose of being able to analyze
 * what happened.
 */
@Entity
public class MentoringAuditLog {

    @Id private Long id;
    @Index private Ref<OxygenUser> student;
    @Index private Ref<OxygenUser> buddy;
    @Nullable private Ref<OxygenUser> supervisingBuddy;
    private Ref<Mission> mission;
    private boolean isSuccess;
    @Index private Long date;

    public MentoringAuditLog() {
    }

    public MentoringAuditLog(
            Ref<OxygenUser> student,
            Ref<OxygenUser> buddy,
            Ref<OxygenUser> supervisingBuddy,
            Ref<Mission> mission,
            boolean isSuccess,
            Long date) {

        this.buddy = buddy;
        this.date = date;
        this.id = id;
        this.isSuccess = isSuccess;
        this.mission = mission;
        this.student = student;
        this.supervisingBuddy = supervisingBuddy;
    }

    public Ref<OxygenUser> getBuddy() {
        return buddy;
    }

    public void setBuddy(Ref<OxygenUser> buddy) {
        this.buddy = buddy;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Ref<Mission> getMission() {
        return mission;
    }

    public void setMission(Ref<Mission> mission) {
        this.mission = mission;
    }

    public Ref<OxygenUser> getStudent() {
        return student;
    }

    public void setStudent(Ref<OxygenUser> student) {
        this.student = student;
    }

    @Nullable
    public Ref<OxygenUser> getSupervisingBuddy() {
        return supervisingBuddy;
    }

    public void setSupervisingBuddy(@Nullable Ref<OxygenUser> supervisingBuddy) {
        this.supervisingBuddy = supervisingBuddy;
    }
}
