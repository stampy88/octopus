package org.lisapark.octopus.core.processor;

import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.runtime.ProcessorContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class CompiledProcessor<MEMORY_TYPE> {
    private final List<ProcessorInput> inputs;
    private final ProcessorOutput output;
    private final UUID id;

    protected CompiledProcessor(Processor<MEMORY_TYPE> processor) {
        this.id = processor.getId();
        this.inputs = processor.getInputs();
        this.output = processor.getOutput();
    }

    public UUID getId() {
        return id;
    }

    public List<ProcessorInput> getInputs() {
        return inputs;
    }

    public ProcessorOutput getOutput() {
        return output;
    }

    public abstract Object processEvent(ProcessorContext<MEMORY_TYPE> ctx, Map<Integer, Event> eventsByInputId);
}
