package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;

import java.io.IOException;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that allows an admin to add point to a student's account.
 */
public class AddPointsByAdminAction extends ServerAction {

    public void addPointsByAdmin(
            OxygenUser adminUser,
            Long studentId,
            String pointTypeString,
            int addedPoints)
            throws UnauthorizedException, IOException {

        // Look up data.
        OxygenUser student = loadUserById(studentId);
        UserPoints.PointType pointType = UserPoints.PointType.valueOf(pointTypeString);

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
                Ref.create(Key.create(OxygenUser.class, adminUser.getId())),
                pointType,
                addedPoints);
        ofy().save().entity(auditLog).now();

        // Send notification to the student via Firebase message.
        UserBean studentBean = stripForSafety(new UserBean(student));
        FirebaseUtil.sendPointsUpdateToStudent(studentBean);
    }
}
