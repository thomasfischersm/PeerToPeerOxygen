package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

/**
 * Objectify entity that tracks which mission a particular user has completed.
 */
@Entity
@Cache
public class MissionCompletion {

    @Id private Long id;
    @Load private Ref<Mission> mission;
    private int studyCount;
    private int mentorCount;
    private boolean studyComplete = false;
    private boolean mentorCheckoutComplete = false;

    public MissionCompletion() {
    }

    public MissionCompletion(
            Long id,
            Ref<Mission> mission,
            int studyCount,
            int mentorCount,
            boolean studyComplete,
            boolean mentorCheckoutComplete) {

        this.id = id;
        this.mentorCount = mentorCount;
        this.mission = mission;
        this.studyCount = studyCount;
        this.studyComplete = studyComplete;
        this.mentorCheckoutComplete = mentorCheckoutComplete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMentorCount() {
        return mentorCount;
    }

    public void setMentorCount(int mentorCount) {
        this.mentorCount = mentorCount;
    }

    public Ref<Mission> getMission() {
        return mission;
    }

    public void setMission(Ref<Mission> mission) {
        this.mission = mission;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(int studyCount) {
        this.studyCount = studyCount;
    }

    public boolean isMentorCheckoutComplete() {
        return mentorCheckoutComplete;
    }

    public void setMentorCheckoutComplete(boolean mentorCheckoutComplete) {
        this.mentorCheckoutComplete = mentorCheckoutComplete;
    }

    public boolean isStudyComplete() {
        return studyComplete;
    }

    public void setStudyComplete(boolean studyComplete) {
        this.studyComplete = studyComplete;
    }
}
