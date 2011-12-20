package org.matrixlab.octopus.core.external.source;

import com.google.common.collect.Lists;
import org.matrixlab.octopus.core.AbstractNode;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.external.ExternalSource;

import java.util.LinkedList;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class TestSource extends AbstractNode implements ExternalSource {

    private final LinkedList<Event> events = Lists.newLinkedList();
    private final EventType eventType;

    public TestSource(String name, String description, EventType eventType) {
        super(name, description);
        this.eventType = eventType;
    }

    private TestSource(TestSource copyFromSource) {
        super(copyFromSource.getName(), copyFromSource.getDescription());
        // todo do we need to reproduce this?
        this.eventType = copyFromSource.eventType;
        this.events.addAll(copyFromSource.events);
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

    @Override
    public TestSource newInstance() {
        return new TestSource(this);
    }
}
