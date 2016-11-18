package com.playposse.peertopeeroxygen.backend.beans;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;

import java.util.ArrayList;
import java.util.List;

/**
 * A bean for the endpoint that transmits domain meta information.
 */
public class DomainBean {

    private Long id;
    private String name;
    private String description;
    private String invitationCode;
    private MasterUserBean ownerBean;
    private boolean isPublic = false;
    private List<MissionLadderBean> missionLadderBeans = new ArrayList<>();

    public DomainBean() {
    }

    public DomainBean(Domain domain) {
        id = domain.getId();
        name = domain.getName();
        description = domain.getDescription();
        invitationCode = domain.getInvitationCode();
        ownerBean = new MasterUserBean(domain.getOwnerRef().get());
        isPublic = domain.isPublic();

        if (domain.getMissionLadderRefs() != null) {
            for (Ref<MissionLadder> missionLadderRef : domain.getMissionLadderRefs()) {
                missionLadderBeans.add(new MissionLadderBean(missionLadderRef.get()));
            }
        }
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

    public MasterUserBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(MasterUserBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<MissionLadderBean> getMissionLadderBeans() {
        return missionLadderBeans;
    }

    public void setMissionLadderBeans(List<MissionLadderBean> missionLadderBeans) {
        this.missionLadderBeans = missionLadderBeans;
    }

    public Domain toEntity() {
        Ref<MasterUser> ownerRef = Ref.create(Key.create(MasterUser.class, ownerBean.getId()));
        Domain domain = new Domain(name, description, invitationCode, ownerRef, isPublic());

        if (domain.getMissionLadderRefs() != null) {
            domain.getMissionLadderRefs().clear();
        } else {
            domain.setMissionLadderRefs(
                    new ArrayList<Ref<MissionLadder>>(missionLadderBeans.size()));
        }

        if (missionLadderBeans != null) {

            for (MissionLadderBean missionLadderBean : missionLadderBeans) {
                Ref<MissionLadder> missionLadderRef =
                        Ref.create(Key.create(MissionLadder.class, missionLadderBean.getId()));
                domain.getMissionLadderRefs().add(missionLadderRef);
            }
        }

        return domain;
    }
}
