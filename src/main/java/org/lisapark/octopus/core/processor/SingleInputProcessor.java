package org.lisapark.octopus.core.processor;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.UUID;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public abstract class SingleInputProcessor<MEMORY_TYPE> extends Processor<MEMORY_TYPE> {

    private ProcessorInput input;

    protected SingleInputProcessor(UUID id, String name, String description) {
        super(id, name, description);
    }

    protected SingleInputProcessor(UUID id, SingleInputProcessor<MEMORY_TYPE> copyFromProcessor) {
        super(id, copyFromProcessor);

        setInput(copyFromProcessor.getInput().copyOf());
    }

    protected SingleInputProcessor(SingleInputProcessor<MEMORY_TYPE> copyFromProcessor) {
        super(copyFromProcessor);
        setInput(copyFromProcessor.getInput().copyOf());
    }

    public ProcessorInput getInput() {
        return input;
    }

    @Override
    public List<ProcessorInput> getInputs() {
        return ImmutableList.of(input);
    }

    protected void setInput(ProcessorInput.Builder input) {
        setInput(input.build());
    }

    protected void setInput(ProcessorInput input) {
        this.input = input;
    }
}
