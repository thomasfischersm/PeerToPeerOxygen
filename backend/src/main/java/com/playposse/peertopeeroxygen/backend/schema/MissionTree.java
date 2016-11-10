package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.playposse.peertopeeroxygen.backend.schema.util.MigrationConstants;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Objectify entity that represents a mission tree. A mission tree is a collection of missions
 * that ends with beating the mission boss. The missions have a theme. The theme can have multiple
 * levels of mission trees. Each level is represented with its own mission tree.
 */
@Entity
@Cache
public class MissionTree {

    @Id private Long id;
    private String name;
    private String description;
    private int level;
    @Index @Load @Nullable Ref<Mission> bossMissionRef;
    @Load private List<Ref<Mission>> missions = new ArrayList<>();
    @Index private Ref<Domain> domainRef;

    /**
     * List of missions that are required to be completed before attempting to beat the mission
     * boss.
     */
    @Load private List<Ref<Mission>> requiredMissions = new ArrayList<>();

    /**
     * Default constructor for objectify.
     */
    public MissionTree() {
    }

    public MissionTree(
            Long id,
            String name,
            String description,
            int level,
            Ref<Mission> bossMissionRef,
            Ref<Domain> domainRef) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.bossMissionRef = bossMissionRef;
        this.domainRef = domainRef;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Ref<Mission>> getMissions() {
        return missions;
    }

    public List<Ref<Mission>> getRequiredMissions() {
        return requiredMissions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Nullable
    public Ref<Mission> getBossMissionRef() {
        return bossMissionRef;
    }

    public void setBossMissionRef(@Nullable Ref<Mission> bossMissionRef) {
        this.bossMissionRef = bossMissionRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ref<Domain> getDomainRef() {
        return domainRef;
    }

    public void setDomainRef(Ref<Domain> domainRef) {
        this.domainRef = domainRef;
    }

    @OnLoad
    public void migrateToMultiDomainSupport() {
        if ((domainRef == null) && (MigrationConstants.DEFAULT_DOMAIN != null)) {
            domainRef = RefUtil.createDomainRef(MigrationConstants.DEFAULT_DOMAIN);
            ofy().save().entity(this).now();
        }
    }
}
