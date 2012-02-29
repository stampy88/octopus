package org.lisapark.octopus.repository;

import org.lisapark.octopus.OctopusException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class RepositoryException extends OctopusException {

    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
