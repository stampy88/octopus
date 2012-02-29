package org.lisapark.octopus;

/**
 * This is the top level checked {@link Exception} in the Octopus application. All other exception types should
 * be a subclass of this exception.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class OctopusException extends Exception {

    public OctopusException(Throwable cause) {
        super(cause);
    }

    public OctopusException(String message) {
        super(message);
    }

    public OctopusException(String message, Throwable cause) {
        super(message, cause);
    }
}
