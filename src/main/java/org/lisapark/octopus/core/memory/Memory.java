package org.lisapark.octopus.core.memory;

import org.lisapark.octopus.core.event.Event;

import java.util.Collection;

/**
 * A {@link Memory} is used to store {@link Event}s for a {@link org.lisapark.octopus.core.processor.Processor}.
 * <p/>
 * Some {@link org.lisapark.octopus.core.processor.Processor}s required memory to store computed or temporary values
 * in order to compute a computation.  An example of this is the {@link org.lisapark.octopus.core.processor.Sma} that
 * contains a window length which is a number of {@link Event}s for which the average should be computed over.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Memory<T> {

    void add(T value);

    boolean remove(T value);

    Collection<T> values();
}
