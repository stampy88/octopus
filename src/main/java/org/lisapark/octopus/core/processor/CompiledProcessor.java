package org.lisapark.octopus.core.processor;

import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.runtime.ProcessorContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class CompiledProcessor<MEMORY_TYPE> {
    private final List<ProcessorInput> inputs;
    private final List<ProcessorJoin> joins;
    private final ProcessorOutput output;
    private final UUID id;

    protected CompiledProcessor(Processor<MEMORY_TYPE> processor) {
        this.id = processor.getId();
        this.inputs = processor.getInputs();
        this.joins = processor.getJoins();
        this.output = processor.getOutput();
    }

    public UUID getId() {
        return id;
    }

    public List<ProcessorJoin> getJoins() {
        return joins;
    }

    public List<ProcessorInput> getInputs() {
        return inputs;
    }

    public ProcessorOutput getOutput() {
        return output;
    }

    public ProcessorJoin getJoinForInput(ProcessorInput input) {
        checkArgument(input != null, "input cannot be null");

        ProcessorJoin join = null;
        for (ProcessorJoin candidate : joins) {
            if (candidate.getFirstInput().equals(input) || candidate.getSecondInput().equals(input)) {
                join = candidate;
                break;
            }
        }

        return join;
    }

    public abstract Object processEvent(ProcessorContext<MEMORY_TYPE> ctx, Map<Integer, Event> eventsByInputId);
}
