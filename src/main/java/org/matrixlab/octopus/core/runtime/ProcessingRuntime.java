package org.matrixlab.octopus.core.runtime;

import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ProcessingRuntime {
    void start();

    void sendEvent(Event event, EventType eventType);
}
