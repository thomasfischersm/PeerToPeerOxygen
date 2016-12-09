package com.playposse.peertopeeroxygen.backend.exceptions;

import com.google.api.server.spi.ServiceException;

/**
 * An {@link Exception} thrown by the EndPoint when the client tries to create a private domain
 * with a name that already exists.
 */
public class DuplicateDomainNameException extends ServiceException {

    public DuplicateDomainNameException(String domainName) {
        super(403, domainName);
    }
}
