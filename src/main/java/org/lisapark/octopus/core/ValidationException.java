package org.lisapark.octopus.core;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ValidationException extends RuntimeException {

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