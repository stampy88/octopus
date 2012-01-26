package org.lisapark.octopus.core.source.external;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;

import java.util.LinkedList;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class TestSource extends ExternalSource {

    private static final String DEFAULT_NAME = "Test Source";
    private static final String DEFAULT_DESCRIPTION = "Source for generating predefined events";

    private final LinkedList<Event> events = Lists.newLinkedList();

    public TestSource(UUID id, String name, String description) {
        super(id, name, description);
    }

    private TestSource(UUID id, TestSource copyFromSource) {
        super(id, copyFromSource);
        this.events.addAll(copyFromSource.events);
    }

    public TestSource(TestSource copyFromSource) {
        super(copyFromSource);
        this.events.addAll(copyFromSource.events);
    }

    public void setEventType(EventType eventType) {
        getOutput().setEventType(eventType);
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public TestSource copyOf() {
        return new TestSource(this);
    }

    @Override
    public TestSource newInstance() {
        UUID sourceId = UUID.randomUUID();
        return new TestSource(sourceId, this);
    }

    public static TestSource newTemplate() {
        UUID sourceId = UUID.randomUUID();

        TestSource testSource = new TestSource(sourceId, DEFAULT_NAME, DEFAULT_DESCRIPTION);
        testSource.setOutput(Output.outputWithId(1).setName("Output"));

        return testSource;
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
