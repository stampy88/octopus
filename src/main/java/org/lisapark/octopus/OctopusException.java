package org.lisapark.octopus;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
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
