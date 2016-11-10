package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * An Objectify entity that represents a domain, e.g. Argentine Tango
 */
@Entity
@Cache
public class Domain {

    @Id private Long id;
    private String name;
    private String description;
    @Index private String invitationCode;
    private Ref<MasterUser> ownerRef;
    private Long created = System.currentTimeMillis();
    @Index private boolean isPublic = false;
    private List<Ref<MissionLadder>> missionLadderRefs = new ArrayList<>();

    public Domain() {
    }

    public Domain(String name, String description, String invitationCode, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.invitationCode = invitationCode;
        this.isPublic = isPublic;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Ref<MasterUser> getOwnerRef() {
        return ownerRef;
    }

    public void setOwnerRef(Ref<MasterUser> ownerRef) {
        this.ownerRef = ownerRef;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<Ref<MissionLadder>> getMissionLadderRefs() {
        return missionLadderRefs;
    }

    public void setMissionLadderRefs(List<Ref<MissionLadder>> missionLadderRefs) {
        this.missionLadderRefs = missionLadderRefs;
    }
}
