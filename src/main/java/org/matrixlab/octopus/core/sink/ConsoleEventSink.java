package org.matrixlab.octopus.core.sink;

import org.matrixlab.octopus.core.EventSink;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ConsoleEventSink implements EventSink {
    @Override
    public void eventReceived(EventType eventType, Event event) {
        System.out.printf("%s - %s\n", eventType, event);
    }
}
