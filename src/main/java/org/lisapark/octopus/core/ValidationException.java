package org.lisapark.octopus.core;

import org.lisapark.octopus.OctopusException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ValidationException extends OctopusException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
