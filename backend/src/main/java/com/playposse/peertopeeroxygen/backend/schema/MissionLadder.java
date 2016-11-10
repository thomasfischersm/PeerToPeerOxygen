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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Objectify Entity that models missions ladders. A rung of mission levels has a type, e.g.
 * technique missions. The {@link MissionLadder} represents the list of {@link MissionTree}s.
 *
 * <p>1 Mission ladder has multiple levels (mission tree). The mission tree are the individual
 * missions that have to be completed order by their prerequisites.
 */
@Entity
@Cache
public class MissionLadder {

    @Id private Long id;
    private String name;
    private String description;
    @Load private List<Ref<MissionTree>> missionTreeRefs = new ArrayList<>();
    @Index private Ref<Domain> domainRef;

    /**
     * Default constructor for Objectify.
     */
    public MissionLadder() {
    }

    /**
     * Constructor for converting bean to entity.
     */
    public MissionLadder(Long id, String name, String description, Ref<Domain> domainRef) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.domainRef = domainRef;
    }

    /**
     * Sorts the {@link MissionTree}s when the entity is loaded by level.
     */
    @OnLoad
    public void onLoad() {
        Collections.sort(missionTreeRefs, new MissionTreeRefComparator());
    }

    public List<Ref<MissionTree>> getMissionTreeRefs() {
        return missionTreeRefs;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    private static final class MissionTreeRefComparator implements Comparator<Ref<MissionTree>> {

        @Override
        public int compare(Ref<MissionTree> missionTreeRef0, Ref<MissionTree> missionTreeRef1) {
            MissionTree missionTree0 = missionTreeRef0.get();
            MissionTree missionTree1 = missionTreeRef1.get();

            if (missionTree0 == null) {
                return 1;
            } else if (missionTree1 == null) {
                return -1;
            } else {
                return missionTree0.getLevel() - missionTree1.getLevel();
            }
        }
    }
}
