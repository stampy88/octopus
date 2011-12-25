package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.event.Event;
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
    private final UUID processorId;

    public CompiledProcessor(List<Input> inputs) {
        this(inputs, null, null);
    }

    public CompiledProcessor(List<Input> inputs, Output output) {
        this(inputs, output, null);
    }

    public CompiledProcessor(List<Input> inputs, Output output, UUID processorId) {
        this.inputs = inputs;
        this.output = output;
        this.processorId = processorId;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public Output getOutput() {
        return output;
    }

    public boolean generatesOutput() {
        return output != null && processorId != null;
    }

    public UUID getId() {
        return processorId;
    }

    public abstract Object processEvent(Memory<MEMORY_TYPE> memory, Map<Integer, Event> eventsByInputId);
}
