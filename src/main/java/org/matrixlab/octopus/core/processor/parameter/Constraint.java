package org.matrixlab.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Constraint<T> {

    void validate(String name, T value) throws ConstraintException;
}
