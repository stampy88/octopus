package org.lisapark.octopus.core;

/**
 * A {@link Validatable} is capable of validating itself to ensure its internal state is valid. If it is <b>not</b>,
 * said object should throw a {@link org.lisapark.octopus.core.ValidationException}
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Validatable {

    /**
     * Implementers validate the correctness of the current state and throw a {@link ValidationException} if they
     * are not.
     *
     * @throws ValidationException thrown if this object is not valid
     */
    void validate() throws ValidationException;
}
