package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;

/**
 * Equivalent of {@link MissionCompletion} for transport across the network.
 */
public class MissionCompletionBean {

    private Long id;
    private Long missionId;
    private int studyCount;
    private int mentorCount;

    public MissionCompletionBean() {
    }

    public MissionCompletionBean(MissionCompletion missionCompletion) {
        id = missionCompletion.getId();
        missionId = missionCompletion.getMission().getKey().getId();
        studyCount = missionCompletion.getStudyCount();
        mentorCount = missionCompletion.getMentorCount();
    }

    public MissionCompletion toEntity() {
        Ref<Mission> missionRef = Ref.create(Key.create(Mission.class, missionId));
        return new MissionCompletion(id, missionRef, studyCount, mentorCount);
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

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(int studyCount) {
        this.studyCount = studyCount;
    }
}
