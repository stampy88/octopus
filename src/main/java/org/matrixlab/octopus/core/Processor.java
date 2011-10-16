package org.matrixlab.octopus.core;

import org.matrixlab.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Processor {

    /**
     * Returns the {@link org.matrixlab.octopus.core.event.EventType} that this processor outputs.
     * This is generally used for sending events a {@link org.matrixlab.octopus.core.EventProcessingNode}
     * to it's attached {@link org.matrixlab.octopus.core.EventSink}s.
     *
     * @return type based on processor
     */
    EventType getOutputEventType();
}
