package org.lisapark.octopus.core.parameter;

import org.lisapark.octopus.core.ValidationException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ConstraintException extends ValidationException {

    public ConstraintException(String message) {
        super(message);
    }

    public ConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

}
