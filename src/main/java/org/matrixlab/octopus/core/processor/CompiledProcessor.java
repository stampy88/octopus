package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.memory.Memory;

import java.util.List;
import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class CompiledProcessor<MEMORY_TYPE> {
    private final List<Input> inputs;
    private final Output output;
    private final EventType outputEventType;

    protected CompiledProcessor(Processor<MEMORY_TYPE> processor) {
        this.inputs = processor.getInputs();
        this.output = processor.getOutput();
        this.outputEventType = processor.getOutputEventType();
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
