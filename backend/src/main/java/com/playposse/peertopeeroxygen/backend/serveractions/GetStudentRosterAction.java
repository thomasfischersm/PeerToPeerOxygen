package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.beans.UserBean;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An action that downloads all the students.
 */
public class GetStudentRosterAction extends ServerAction {
    public List<UserBean> getStudentRoster() {
        List<OxygenUser> users = ofy()
                .load()
//                .group(UserPoints.class)
                .type(OxygenUser.class)
                .list();

        List<UserBean> userBeans = new ArrayList<>();
        for (OxygenUser user : users) {
            userBeans.add(new UserBean(user));
        }

        return stripForSafety(userBeans);
    }
}
