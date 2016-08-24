package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.server.spi.response.UnauthorizedException;
import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.firebase.FirebaseUtil;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;

import java.io.IOException;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action that allows an admin to add point to a student's account.
 */
public class AddPointsByAdminAction extends ServerAction {

    public void addPointsByAdmin(
            Long sessionId,
            Long studentId,
            String pointTypeString,
            int addedPoints)
            throws UnauthorizedException, IOException {

        OxygenUser student = loadUserById(studentId);

        UserPoints userPoints = null;
        for (Map.Entry<UserPoints.PointType, UserPoints> entry : student.getPoints().entrySet()) {
            if (entry.getKey().name().equals(pointTypeString)) {
                userPoints = entry.getValue();
                userPoints.setCount(userPoints.getCount() + addedPoints);
                break;
            }
        }

        if (userPoints == null) {
            UserPoints.PointType pointType = UserPoints.PointType.valueOf(pointTypeString);
            userPoints = new UserPoints(pointType, addedPoints);
            student.getPoints().put(pointType, userPoints);
        }

        UserBean studentBean = stripForSafety(new UserBean(student));
        FirebaseUtil.sendPointsUpdateToStudent(studentBean);

        ofy().save().entity(student).now();
    }
}
