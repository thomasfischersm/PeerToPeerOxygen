package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Objectify entity that describes a mission.
 */
@Entity
@Cache
public class Mission {

    @Id private Long id;
    private String name;
    private String studentInstruction;
    private String buddyInstruction;
    private int minimumStudyCount = 1;
    @Nullable private String studentYouTubeVideoId;
    @Nullable private String buddyYouTubeVideoId;
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
    public Mission(
            Long id,
            String name,
            String studentInstruction,
            String buddyInstruction,
            int minimumStudyCount,
            String studentYouTubeVideoId,
            String buddyYouTubeVideoId) {

        this.id = id;
        this.buddyInstruction = buddyInstruction;
        this.name = name;
        this.studentInstruction = studentInstruction;
        this.minimumStudyCount = minimumStudyCount;
        this.studentYouTubeVideoId = studentYouTubeVideoId;
        this.buddyYouTubeVideoId = buddyYouTubeVideoId;
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

    public int getMinimumStudyCount() {
        return minimumStudyCount;
    }

    public void setMinimumStudyCount(int minimumStudyCount) {
        this.minimumStudyCount = minimumStudyCount;
    }

    @Nullable
    public String getBuddyYouTubeVideoId() {
        return buddyYouTubeVideoId;
    }

    public void setBuddyYouTubeVideoId(@Nullable String buddyYouTubeVideoId) {
        this.buddyYouTubeVideoId = buddyYouTubeVideoId;
    }

    @Nullable
    public String getStudentYouTubeVideoId() {
        return studentYouTubeVideoId;
    }

    public void setStudentYouTubeVideoId(@Nullable String studentYouTubeVideoId) {
        this.studentYouTubeVideoId = studentYouTubeVideoId;
    }

    public List<Ref<Mission>> getRequiredMissions() {
        return requiredMissions;
    }

    public Map<UserPoints.PointType, Integer> getPointCostMap() {
        return pointCostMap;
    }
}
