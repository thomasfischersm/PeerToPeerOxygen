package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseServerAction;
import com.playposse.peertopeeroxygen.backend.firebase.SendPointsUpdateToStudentServerAction;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import java.io.IOException;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that allows an admin to add point to a student's account.
 */
public class AddPointsByAdminServerAction extends ServerAction {

    public void addPointsByAdmin(
            Long sessionId,
            Long studentId,
            String pointTypeString,
            int addedPoints,
            Long domainId)
            throws UnauthorizedException, IOException, BadRequestException {

        // Look up data.
        MasterUser adminMasterUser = loadMasterUserBySessionId(sessionId);
        OxygenUser adminOxygenUser = findOxygenUserByDomain(adminMasterUser, domainId);
        OxygenUser student = loadOxygenUserById(studentId, domainId);
        UserPoints.PointType pointType = UserPoints.PointType.valueOf(pointTypeString);

        // Check security.
        protectByAdminCheck(adminMasterUser, adminOxygenUser, domainId);
        verifyUserByDomain(student, domainId);

        // Instantiate data structures as necessary.
        UserPoints userPoints = null;
        for (Map.Entry<UserPoints.PointType, UserPoints> entry : student.getPoints().entrySet()) {
            if (entry.getKey().name().equals(pointTypeString)) {
                userPoints = entry.getValue();
                userPoints.setCount(userPoints.getCount() + addedPoints);
                break;
            }
        }

        if (userPoints == null) {
            userPoints = new UserPoints(pointType, addedPoints);
            student.getPoints().put(pointType, userPoints);
        }

        // Save the changes to the data store.
        ofy().save().entity(student).now();

        // Record the transaction for auditing.
        PointsTransferAuditLog auditLog = new PointsTransferAuditLog(
                PointsTransferAuditLog.PointsTransferType.awardedByAdmin,
                Ref.create(Key.create(OxygenUser.class, student.getId())),
                Ref.create(Key.create(OxygenUser.class, adminOxygenUser.getId())),
                pointType,
                addedPoints,
                RefUtil.createDomainRef(domainId));
        ofy().save().entity(auditLog).now();

        // Send notification to the student via Firebase message.
        SendPointsUpdateToStudentServerAction.sendPointsUpdateToStudent(student);
    }
}
