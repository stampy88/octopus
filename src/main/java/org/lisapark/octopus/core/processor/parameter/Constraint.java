package org.lisapark.octopus.core.processor.parameter;

import org.lisapark.octopus.core.Reproducible;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Constraint<T> extends Reproducible {

    void validate(String name, T value) throws ConstraintException;

    Constraint<T> newInstance();

    Constraint<T> copyOf();
}
