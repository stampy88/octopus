package org.matrixlab.octopus.core;

/**
 * This is a general exception that can be thrown by a {@link org.matrixlab.octopus.core.processor.CompiledProcessor}
 * while processing events.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingException extends RuntimeException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
