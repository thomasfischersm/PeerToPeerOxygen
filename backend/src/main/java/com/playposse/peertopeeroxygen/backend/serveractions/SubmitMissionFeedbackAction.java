package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that receives mission feedback from the user.
 */
public class SubmitMissionFeedbackAction extends ServerAction {

    public static void submitMissionFeedback(
            Long sessionId,
            Long missionId,
            int rating,
            @Nullable String comment) throws UnauthorizedException {

        OxygenUser user = loadUserBySessionId(sessionId);

        // Update mission stats
        MissionStats missionStats = getMissionStats(missionId);
        missionStats.addRating(rating);
        ofy().save().entity(missionStats);

        // Save mission feedback
        if ((comment != null) && (comment.trim().length() > 0)) {
            Ref<OxygenUser> userRef = Ref.create(Key.create(OxygenUser.class, user.getId()));
            Ref<Mission> missionRef = Ref.create(Key.create(Mission.class, missionId));
            MissionFeedback missionFeedback =
                    new MissionFeedback(missionRef, userRef, rating, comment.trim());
            ofy().save().entity(missionFeedback);
        }
    }
}
