package org.lisapark.octopus.core.parameter;

import org.lisapark.octopus.core.Copyable;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Constraint<T> extends Copyable {

    void validate(String name, T value) throws ConstraintException;

    Constraint<T> copyOf();
}
