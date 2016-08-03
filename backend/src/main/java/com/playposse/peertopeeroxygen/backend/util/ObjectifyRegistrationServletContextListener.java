package com.playposse.peertopeeroxygen.backend.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A {@link ServletContextListener} that regsiters the Objectify entities on startup.
 */
public class ObjectifyRegistrationServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        ObjectifyService.register(Message.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do.
    }
}
