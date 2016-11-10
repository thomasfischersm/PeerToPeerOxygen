package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.beans.MissionStatsBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action to get all {@link MissionStats}.
 */
public class GetAllMissionStatsServerAction extends ServerAction {

    public static List<MissionStatsBean> getAllMissionStats(Long sessionId, Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        Key<Domain> domainKey = Key.create(Domain.class, domainId);
        List<MissionStats> missionStatsList =
                ofy().load().type(MissionStats.class).filter("domainRef =", domainKey).list();

        ArrayList<MissionStatsBean> missionStatsBeanList =
                new ArrayList<>(missionStatsList.size());
        for (MissionStats missionStats : missionStatsList) {
            if (missionStats.getMissionRef().get() == null) {
                // The mission must have been deleted.
                continue;
            }
            missionStatsBeanList.add(new MissionStatsBean(missionStats));
        }

        return missionStatsBeanList;
    }
}
