package com.playposse.peertopeeroxygen.backend.util;

import com.googlecode.objectify.ObjectifyService;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.LevelCompletion;
import com.playposse.peertopeeroxygen.backend.schema.LoanerDevice;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.MentoringAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.MissionCompletion;
import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;
import com.playposse.peertopeeroxygen.backend.schema.MissionTree;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.PointsTransferAuditLog;
import com.playposse.peertopeeroxygen.backend.schema.Practica;
import com.playposse.peertopeeroxygen.backend.schema.UserPoints;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A {@link ServletContextListener} that registers the Objectify entities on startup.
 */
public class ObjectifyRegistrationServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectifyService.register(Domain.class);
        ObjectifyService.register(LevelCompletion.class);
        ObjectifyService.register(LoanerDevice.class);
        ObjectifyService.register(MasterUser.class);
        ObjectifyService.register(MentoringAuditLog.class);
        ObjectifyService.register(Mission.class);
        ObjectifyService.register(MissionCompletion.class);
        ObjectifyService.register(MissionFeedback.class);
        ObjectifyService.register(MissionLadder.class);
        ObjectifyService.register(MissionStats.class);
        ObjectifyService.register(MissionTree.class);
        ObjectifyService.register(OxygenUser.class);
        ObjectifyService.register(PointsTransferAuditLog.class);
        ObjectifyService.register(Practica.class);
        ObjectifyService.register(UserPoints.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do.
    }

    public static void bootStrapFirstDomain() {
        List<Domain> domains = ofy().load().type(Domain.class).list();
        if (domains.size() > 0) {
            // The first domain has already been created. Skip this.
            return;
        }

        Domain domain = new Domain(
                "Argentine Tango",
                "Tango is one of the most beautiful, elegant, and sensual dance of modern times.",
                InvitationCodeGenerator.generateCode(),
                true);
        ofy().save().entity(domain).now();
    }
}
