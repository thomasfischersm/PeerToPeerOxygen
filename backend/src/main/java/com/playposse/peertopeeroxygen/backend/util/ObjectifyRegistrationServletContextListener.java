package com.playposse.peertopeeroxygen.backend.util;

import com.googlecode.objectify.ObjectifyService;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionBoss;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A {@link ServletContextListener} that registers the Objectify entities on startup.
 */
public class ObjectifyRegistrationServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectifyService.register(MentoringAuditLog.class);
        ObjectifyService.register(Mission.class);
        ObjectifyService.register(MissionBoss.class);
        ObjectifyService.register(MissionCompletion.class);
        ObjectifyService.register(MissionLadder.class);
        ObjectifyService.register(MissionTree.class);
        ObjectifyService.register(OxygenUser.class);
        ObjectifyService.register(PointsTransferAuditLog.class);
        ObjectifyService.register(UserPoints.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do.
    }
}
