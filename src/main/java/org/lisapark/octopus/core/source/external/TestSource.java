package org.lisapark.octopus.core.source.external;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.AbstractNode;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;

import java.util.LinkedList;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class TestSource extends AbstractNode implements ExternalSource {

    private final LinkedList<Event> events = Lists.newLinkedList();
    private final EventType eventType;

    public TestSource(UUID id, String name, String description, EventType eventType) {
        super(id, name, description);
        this.eventType = eventType;
    }

    private TestSource(UUID id, TestSource copyFromSource) {
        super(id, copyFromSource);
        // todo do we need to reproduce this?
        this.eventType = copyFromSource.eventType;
        this.events.addAll(copyFromSource.events);
    }

    public TestSource(TestSource copyFromSource) {
        super(copyFromSource);
        this.eventType = copyFromSource.eventType;
        this.events.addAll(copyFromSource.events);
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public EventType getOutputEventType() {
        return eventType;
    }

    @Override
    public TestSource newInstance() {
        UUID sourceId = UUID.randomUUID();
        return new TestSource(sourceId, this);
    }

    @Override
    public TestSource copyOf() {
        return new TestSource(this);
    }

    @Override
    public CompiledExternalSource compile() throws ValidationException {
        return new CompiledTestSource(copyOf());
    }

    static class CompiledTestSource implements CompiledExternalSource {

        private final TestSource source;

        /**
         * Running is declared volatile because it may be access my different threads
         */
        private volatile boolean running;

        public CompiledTestSource(TestSource source) {
            this.source = source;
        }

        @Override
        public void startProcessingEvents(ProcessingRuntime runtime) {
            Thread thread = Thread.currentThread();
            running = true;


            while (!thread.isInterrupted() && running && source.events.size() > 0) {
                Event e = source.events.pop();

                runtime.sendEventFromSource(e, source);
            }
        }

        @Override
        public void stopProcessingEvents() {
            running = false;
        }
    }
}
