package org.matrixlab.octopus.core.source.external;

import com.google.common.collect.Lists;
import org.matrixlab.octopus.core.AbstractNode;
import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.runtime.ProcessingRuntime;

import java.util.LinkedList;
import java.util.List;
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

    @Override
    public TestSource newInstance() {
        return new TestSource(this);
    }

    @Override
    public CompiledExternalSource compile() throws ValidationException {
        return new CompiledTestSource(getId(), events);
    }

    static class CompiledTestSource implements CompiledExternalSource {

        private final LinkedList<Event> events = Lists.newLinkedList();
        private final UUID sourceId;

        /**
         * Running is declared volatile because it may be access my different threads
         */
        private volatile boolean running;

        public CompiledTestSource(UUID sourceId, List<Event> events) {
            this.sourceId = sourceId;
            this.events.addAll(events);
        }

        @Override
        public void startProcessingEvents(ProcessingRuntime runtime) {
            Thread thread = Thread.currentThread();
            running = true;

            while (!thread.isInterrupted() && running && events.size() > 0) {
                Event e = events.pop();

                runtime.sendEvent(e, sourceId);
            }
        }

        @Override
        public void stopProcessingEvents() {
            running = false;
        }
    }
}
