package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.beans.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.beans.DomainBean;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that downloads all the mission data. The Android client calls this on startup.
 */
public class GetMissionDataServerAction extends ServerAction {

    public static CompleteMissionDataBean getMissionData(Long sessionId, Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Look up data.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        Domain domain = oxygenUser.getDomainRef().get();

        Key<Domain> domainKey = Key.create(Domain.class, domainId);
        List<MissionLadder> missionLadders = ofy().load()
                .type(MissionLadder.class)
                .filter("domainRef =", domainKey)
                .list();

        return new CompleteMissionDataBean(
                new UserBean(oxygenUser),
                new DomainBean(domain),
                missionLadders);
    }
}
