package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.MissionBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionDataInvalidationServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;
import java.net.URLEncoder;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves a {@link Mission}.
 */
public class SaveMissionServerAction extends ServerAction {

    public static MissionBean saveMission(
            Long sessionId,
            Long missionLadderId,
            Long missionTreeId,
            MissionBean missionBean,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Verify that the data is for the correct domain.
        if (!domainId.equals(missionBean.getDomainId())) {
            String missionName = URLEncoder.encode(missionBean.getName(), "UTF-8");
            throw new BadRequestException("Tried to save mission '" + missionName
                    + "' to domain " + domainId + " but it was domain "
                    + missionBean.getDomainId());
        }

        Mission mission = missionBean.toEntity();

        MissionLadder missionLadder = ofy().load()
                .type(MissionLadder.class)
                .id(missionLadderId)
                .now();

        MissionTree missionTree = findMissionTree(missionLadder, missionTreeId);

        ofy().save().entity(mission).now();

        if (missionBean.getId() == null) {
            missionTree.getMissions().add(Ref.create(Key.create(Mission.class, mission.getId())));
            ofy().save().entity(missionTree).now();
        }

        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation(domainId);

        return new MissionBean(mission);
    }
}
