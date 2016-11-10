package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendMissionDataInvalidationServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;
import java.net.URLEncoder;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that saves the mission ladder.
 */
public class SaveMissionLadderServerAction extends ServerAction {

    public static MissionLadderBean saveMissionLadder(
            Long sessionId,
            MissionLadderBean missionLadderBean,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        // Verify that the data is for the correct domain.
        if (!domainId.equals(missionLadderBean.getDomainId())) {
            String missionTreeName = URLEncoder.encode(missionLadderBean.getName(), "UTF-8");
            throw new BadRequestException("Tried to save mission ladder '" + missionTreeName
                    + "' to domain " + domainId + " but it was domain "
                    + missionLadderBean.getDomainId());
        }

        MissionLadder missionLadder = missionLadderBean.toEntity();
        ofy().save().entity(missionLadder).now();

        SendMissionDataInvalidationServerAction.sendMissionDataInvalidation(domainId);

        return new MissionLadderBean(missionLadder);
    }
}
