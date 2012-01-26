package org.lisapark.octopus;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProgrammerException extends RuntimeException {

    public ProgrammerException(Throwable cause) {
        super(cause);
    }

    public ProgrammerException(String message) {
        super(message);
    }

    public ProgrammerException(String message, Throwable cause) {
        super(message, cause);
    }
}
