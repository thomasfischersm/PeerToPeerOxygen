package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of {@link MissionTree} for transport across the network.
 */
public class MissionTreeBean {

    private Long id;
    private String name;
    private String description;
    private int level;
    private Long bossMissionId;
    private List<MissionBean> missionBeans = new ArrayList<>();
    private List<Long> requiredMissionIds = new ArrayList<>();

    public MissionTreeBean() {
    }

    public MissionTreeBean(MissionTree missionTree) {
        this.id = missionTree.getId();
        this.name = missionTree.getName();
        this.description = missionTree.getDescription();
        this.level = missionTree.getLevel();

        if (missionTree.getBossMissionRef() != null) {
            bossMissionId = missionTree.getBossMissionRef().getKey().getId();
        } else {
            bossMissionId = null;
        }

        for (Ref<Mission> missionRef : missionTree.getMissions()) {
            Mission mission = missionRef.get();
            if (mission != null) {
                missionBeans.add(new MissionBean(mission));
            }
        }

        for (Ref<Mission> requiredMissionRef : missionTree.getRequiredMissions()) {
            if (requiredMissionRef.get() != null) {
                requiredMissionIds.add(requiredMissionRef.getKey().getId());
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBossMissionId() {
        return bossMissionId;
    }

    public void setBossMissionId(Long bossMissionId) {
        this.bossMissionId = bossMissionId;
    }

    public List<MissionBean> getMissionBeans() {
        return missionBeans;
    }

    public List<Long> getRequiredMissionIds() {
        return requiredMissionIds;
    }

    public void setRequiredMissionIds(List<Long> requiredMissionIds) {
        this.requiredMissionIds = requiredMissionIds;
    }

    public MissionTree toEntity() {
        final Ref<Mission> bossMissionRef;
        if (bossMissionId != null) {
            bossMissionRef = Ref.create(Key.create(Mission.class, bossMissionId));
        } else {
            bossMissionRef = null;
        }

        MissionTree missionTree = new MissionTree(id, name, description, level, bossMissionRef);

        for (MissionBean missionBean : missionBeans) {
            Key<Mission> missionKey = Key.create(Mission.class, missionBean.getId());
            missionTree.getMissions().add(Ref.create(missionKey));
        }

        for (Long requiredMissionId : requiredMissionIds) {
            Key<Mission> requiredMissionKey = Key.create(Mission.class, requiredMissionId);
            missionTree.getRequiredMissions().add(Ref.create(requiredMissionKey));
        }
        return missionTree;
    }
}
