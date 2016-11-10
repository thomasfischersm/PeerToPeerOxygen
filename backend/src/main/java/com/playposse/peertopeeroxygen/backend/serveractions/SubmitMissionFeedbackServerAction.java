package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.createDomainRef;
import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.createMissionRef;
import static com.playposse.peertopeeroxygen.backend.schema.util.RefUtil.createOxygenUserRef;

/**
 * A server action that receives mission feedback from the user.
 */
public class SubmitMissionFeedbackServerAction extends ServerAction {

    public static void submitMissionFeedback(
            Long sessionId,
            Long missionId,
            int rating,
            @Nullable String comment,
            Long domainId) throws UnauthorizedException, BadRequestException {

        MasterUser masterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser oxygenUser = findOxygenUserByDomain(masterUser, domainId);

        // Update mission stats
        MissionStats missionStats = getMissionStats(missionId, domainId);
        missionStats.addRating(rating);
        ofy().save().entity(missionStats);

        // Save mission feedback
        if ((comment != null) && (comment.trim().length() > 0)) {
            MissionFeedback missionFeedback = new MissionFeedback(
                    createMissionRef(missionId),
                    createOxygenUserRef(oxygenUser),
                    rating,
                    comment.trim(),
                    createDomainRef(domainId));
            ofy().save().entity(missionFeedback);
        }
    }
}
