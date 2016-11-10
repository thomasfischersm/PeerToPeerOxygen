package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.createDomainRef;

/**
 * Equivalent of {@link Mission} for transport across the network.
 */
public class MissionBean {

    private Long id;
    private String name;
    private String studentInstruction;
    private String buddyInstruction;
    private int minimumStudyCount;
    @Nullable private String studentYouTubeVideoId;
    @Nullable private String buddyYouTubeVideoId;
    private Long domainId;
    private List<Long> requiredMissionIds = new ArrayList<>();
    private Map<String, Integer> pointCostMap = new HashMap<>();

    public MissionBean() {
    }

    public MissionBean(Mission mission) {
        this.id = mission.getId();
        this.name = mission.getName();
        this.studentInstruction = mission.getStudentInstruction();
        this.buddyInstruction = mission.getBuddyInstruction();
        this.minimumStudyCount = mission.getMinimumStudyCount();
        this.studentYouTubeVideoId = mission.getStudentYouTubeVideoId();
        this.buddyYouTubeVideoId = mission.getBuddyYouTubeVideoId();
        this.domainId = mission.getDomainRef().getKey().getId();

        for (Ref<Mission> requiredMissionRef : mission.getRequiredMissions()) {
            if (requiredMissionRef.get() != null) {
                requiredMissionIds.add(requiredMissionRef.getKey().getId());
            }
        }

        if (pointCostMap != null) {
            Set<Map.Entry<UserPoints.PointType, Integer>> pointMapEntries =
                    mission.getPointCostMap().entrySet();
            for (Map.Entry<UserPoints.PointType, Integer> entry : pointMapEntries) {
                pointCostMap.put(entry.getKey().name(), entry.getValue());
            }
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

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public List<Long> getRequiredMissionIds() {
        return requiredMissionIds;
    }

    public Map<String, Integer> getPointCostMap() {
        return pointCostMap;
    }

    public Mission toEntity() {
        Mission mission = new Mission(
                id,
                name,
                studentInstruction,
                buddyInstruction,
                minimumStudyCount,
                studentYouTubeVideoId,
                buddyYouTubeVideoId,
                createDomainRef(domainId));

        for (Long requiredMissionId : requiredMissionIds) {
            Key<Mission> requiredMissionKey = Key.create(Mission.class, requiredMissionId);
            mission.getRequiredMissions().add(Ref.create(requiredMissionKey));
        }

        for (Map.Entry<String, Integer> entry : pointCostMap.entrySet()) {
            UserPoints.PointType pointType = UserPoints.PointType.valueOf(entry.getKey());
            mission.getPointCostMap().put(pointType, entry.getValue());
        }

        return mission;
    }
}
