package org.matrixlab.octopus.core.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.matrixlab.octopus.core.EventSink;
import org.matrixlab.octopus.core.ExternalEventSource;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;

import java.util.LinkedList;
import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class TestEventSource implements ExternalEventSource {

    private final LinkedList<Event> events = Lists.newLinkedList();
    private final Set<EventSink> sinks = Sets.newHashSet();
    private final EventType eventType;

    public TestEventSource(EventType eventType) {
        this.eventType = eventType;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public void addEventSink(EventSink sink) {
        sinks.add(sink);
    }

    public Event readEvent() {
        if (events.size() > 0) {
            return events.pop();
        }
        return null;
    }
}
