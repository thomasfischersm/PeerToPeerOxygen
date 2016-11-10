package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.playposse.peertopeeroxygen.backend.beans.MissionFeedbackBean;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action to get all {@link MissionFeedback}.
 */
public class GetAllMissionFeedbackServerAction extends ServerAction {

    public static List<MissionFeedbackBean> getAllMissionFeedback(Long sessionId, Long domainId)
            throws UnauthorizedException, BadRequestException {

        // Do security check.
        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);
        protectByAdminCheck(masterUser, oxygenUser, domainId);

        Key<Domain> domainKey = Key.create(Domain.class, domainId);
        List<MissionFeedback> missionFeedbackList =
                ofy().load().type(MissionFeedback.class).filter("domainRef =", domainKey).list();

        ArrayList<MissionFeedbackBean> missionFeedbackBeanList =
                new ArrayList<>(missionFeedbackList.size());
        for (MissionFeedback missionFeedback : missionFeedbackList) {
            if (missionFeedback.getMissionRef().get() == null) {
                // The mission must have been deleted.
                continue;
            }
            MissionFeedbackBean missionFeedbackBean = new MissionFeedbackBean(missionFeedback);

            missionFeedbackBeanList.add(missionFeedbackBean);
        }

        return missionFeedbackBeanList;
    }
}
