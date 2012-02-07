package org.lisapark.octopus.core.parameter;

import org.lisapark.octopus.core.Copyable;
import org.lisapark.octopus.core.Persistable;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public interface Constraint<T> extends Copyable {

    void validate(String name, T value) throws ConstraintException;

    Constraint<T> copyOf();
}
