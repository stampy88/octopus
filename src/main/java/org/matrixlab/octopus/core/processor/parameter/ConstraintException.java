package org.matrixlab.octopus.core.processor.parameter;

import org.matrixlab.octopus.core.ValidationException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ConstraintException extends ValidationException {

    public ConstraintException(Throwable cause) {
        super(cause);
    }

    public ConstraintException(String message) {
        super(message);
    }
}
