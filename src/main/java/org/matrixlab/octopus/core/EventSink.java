package org.matrixlab.octopus.core;

import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface EventSink {

    void eventReceived(EventType eventType, Event event);
}
