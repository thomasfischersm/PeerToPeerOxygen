package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that a buddy calls to notify the server that the mission is complete. Also, the
 * student's Android device is notified with Firebase message.
 */
public class ReportMissionCompleteAction extends ServerAction {

    public static void reportMissionComplete(
            Long sessionId,
            Long studentId,
            Long missionId)
            throws UnauthorizedException, IOException {

        // Load relevant data.
        OxygenUser buddy = loadUserBySessionId(sessionId);
        OxygenUser student = loadUserById(studentId);
        Ref<Mission> missionRef = Ref.create(Key.create(Mission.class, missionId));
//        Mission mission = ofy().load().type(Mission.class).id(missionId).now();

        // TODO: Check if the buddy is allowed to teach the mission.

        // Update student
        if (student.getMissionCompletions().containsKey(missionId)) {
            MissionCompletion completion = student.getMissionCompletions().get(missionId);
            completion.setStudyCount(completion.getStudyCount() + 1);
        } else {
            long completionId = factory().allocateId(MissionCompletion.class).getId();
            MissionCompletion completion =
                    new MissionCompletion(completionId, missionRef, 1, 0);
            student.getMissionCompletions().put(missionId, completion);
        }
        ofy().save().entity(student).now();

        // Update buddy.
        if (buddy.getMissionCompletions().containsKey(missionId)) {
            MissionCompletion completion = buddy.getMissionCompletions().get(missionId);
            completion.setMentorCount(completion.getMentorCount() + 1);
        } else {
            if (buddy.isAdmin()) {
                long completionId = factory().allocateId(MissionCompletion.class).getId();
                MissionCompletion completion =
                        new MissionCompletion(completionId, missionRef, 0, 1);
                buddy.getMissionCompletions().put(missionId, completion);
            } else {
                // This is an error case because a mission has to be studied at least once before being
                // allowed to teach.
                // TODO: throw exception
            }
        }
        ofy().save().entity(buddy).now();

        // Save audit log
        MentoringAuditLog audit = new MentoringAuditLog(
                Ref.create(Key.create(OxygenUser.class, studentId)),
                Ref.create(Key.create(OxygenUser.class, buddy.getId())),
                null,
                missionRef,
                true,
                System.currentTimeMillis());
        ofy().save().entity(audit).now();

        // Send a Firebase message to the student to confirm completion.
        FirebaseUtil.sendMissionCompletionToStudent(
                student.getFirebaseToken(),
                new UserBean(buddy),
                missionId);
    }
}
