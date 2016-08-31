package com.playposse.peertopeeroxygen.backend.exceptions;

import com.google.api.server.spi.ServiceException;

/**
 * An exception fired by the EndPoint when the buddy isn't ready to teach the mission yet.
 */
public class BuddyLacksMissionExperienceException extends ServiceException {

    public BuddyLacksMissionExperienceException(String buddyName) {
        super(403, buddyName);
    }
}
