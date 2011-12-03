package org.matrixlab.octopus.core.external.source;

import com.google.common.collect.Lists;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.external.ExternalSource;

import java.util.LinkedList;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class TestSource implements ExternalSource {

    private final LinkedList<Event> events = Lists.newLinkedList();
    private final EventType eventType;

    public TestSource(EventType eventType) {
        this.eventType = eventType;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public UUID getId() {
        return eventType.getId();
    }

    @Override
    public EventType getOutputEventType() {
        return eventType;
    }

    public Event readEvent() {
        if (events.size() > 0) {
            return events.pop();
        }
        return null;
    }
}
