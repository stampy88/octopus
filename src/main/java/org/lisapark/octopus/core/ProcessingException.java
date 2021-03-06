package org.lisapark.octopus.core;

import org.lisapark.octopus.OctopusException;

/**
 * This is a general exception that can be thrown by a {@link org.lisapark.octopus.core.processor.CompiledProcessor}
 * while processing events.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingException extends OctopusException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
