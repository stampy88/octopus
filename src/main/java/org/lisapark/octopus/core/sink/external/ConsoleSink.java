package org.lisapark.octopus.core.sink.external;

import com.google.common.collect.ImmutableList;
import org.lisapark.octopus.core.AbstractNode;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Persistable;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.runtime.SinkContext;
import org.lisapark.octopus.core.sink.Sink;
import org.lisapark.octopus.core.source.Source;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public class ConsoleSink extends AbstractNode implements ExternalSink {
    private static final String DEFAULT_NAME = "Console";
    private static final String DEFAULT_DESCRIPTION = "Console Output";
    private static final String DEFAULT_INPUT = "Input";

    private Input<Event> input;

    private ConsoleSink(UUID id, String name, String description) {
        super(id, name, description);
        input = Input.eventInputWithId(1);
        input.setName(DEFAULT_INPUT);
        input.setDescription(DEFAULT_INPUT);
    }

    private ConsoleSink(UUID id, ConsoleSink copyFromNode) {
        super(id, copyFromNode);
        input = copyFromNode.getInput().copyOf();
    }

    private ConsoleSink(ConsoleSink copyFromNode) {
        super(copyFromNode);
        this.input = copyFromNode.input.copyOf();
    }

    public Input<Event> getInput() {
        return input;
    }

    @Override
    public List<Input<Event>> getInputs() {
        return ImmutableList.of(input);
    }

    @Override
    public boolean isConnectedTo(Source source) {

        return input.isConnectedTo(source);
    }

    @Override
    public void disconnect(Source source) {
        if (input.isConnectedTo(source)) {
            input.clearSource();
        }
    }

    @Override
    public ConsoleSink newInstance() {
        return new ConsoleSink(UUID.randomUUID(), this);
    }

    @Override
    public ConsoleSink copyOf() {
        return new ConsoleSink(this);
    }

    public static ConsoleSink newTemplate() {
        UUID sinkId = UUID.randomUUID();

        return new ConsoleSink(sinkId, DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    @Override
    public CompiledExternalSink compile() throws ValidationException {
        return new CompiledConsole(copyOf());
    }

    static class CompiledConsole extends CompiledExternalSink {
        protected CompiledConsole(Sink processor) {
            super(processor);
        }

        @Override
        public void processEvent(SinkContext ctx, Map<Integer, Event> eventsByInputId) {
            Event event = eventsByInputId.get(1);
            if (event != null) {
                ctx.getStandardOut().println(event);
            }
        }
    }
}
