package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.Input;
import org.matrixlab.octopus.core.Output;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.memory.Memory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class CompiledProcessor<MEMORY_TYPE> {
    private final List<Input> inputs;
    private final Output output;
    private final EventType outputEventType;
    private final UUID id;

    protected CompiledProcessor(Processor<MEMORY_TYPE> processor) {
        this.id = processor.getId();
        this.inputs = processor.getInputs();
        this.output = processor.getOutput();
        this.outputEventType = processor.getOutputEventType();
    }

    public UUID getId() {
        return id;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public Output getOutput() {
        return output;
    }

    public boolean generatesOutput() {
        return output != null && outputEventType != null;
    }

    public EventType getOutputEventType() {
        return outputEventType;
    }

    public abstract Object processEvent(Memory<MEMORY_TYPE> memory, Map<Integer, Event> eventsByInputId);
}
