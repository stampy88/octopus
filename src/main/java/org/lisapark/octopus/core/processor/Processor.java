package org.lisapark.octopus.core.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.lisapark.octopus.core.AbstractNode;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.memory.MemoryProvider;
import org.lisapark.octopus.core.processor.parameter.Parameter;
import org.lisapark.octopus.core.sink.Sink;
import org.lisapark.octopus.core.source.Source;

import java.util.List;
import java.util.UUID;

/**
 * A {@link Processor} is a program unit that has one or more {@link Input}s and potentially produces an {@link Output}.
 * In addition to {@link Input}s and an {@link Output}, a processor can be configured with additional {@link Parameter}s
 * that affect the behavior of the processor.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.lisapark.octopus.core.Input
 * @see org.lisapark.octopus.core.Output
 * @see org.lisapark.octopus.core.processor.parameter.Parameter
 */
public abstract class Processor<MEMORY_TYPE> extends AbstractNode implements Source, Sink {

    /**
     * A processor will be given zero or more inputs in order to perform its processing; this will be the
     * list of all of these inputs.
     */
    private List<Input> inputs = Lists.newLinkedList();

    // todo event type inside output??
    /**
     * A processor may produce an output after its processing.
     */
    private Output output;
    private EventType outputEventType;

    /**
     * Constructor that takes id of processor, name and description.
     *
     * @param id          of processor
     * @param name        of processor
     * @param description of processor
     */
    protected Processor(UUID id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Copy constructor for creating a <b>new</b> processor based off of the copyFromProcessor. Note that we are using the
     * {@link org.lisapark.octopus.core.Reproducible} interface on {@link Input}s, {@link Parameter}s and
     * {@link Output} if there is one.
     *
     * @param id                of new processor
     * @param copyFromProcessor that we are getting copies from
     */
    protected Processor(UUID id, Processor<MEMORY_TYPE> copyFromProcessor) {
        super(id, copyFromProcessor);

        for (Input input : copyFromProcessor.getInputs()) {
            this.addInput(input.newInstance());
        }

        // todo no side effect? 
        this.setOutput(copyFromProcessor.getOutput().newInstance());
    }

    /**
     * Copy constructor for creating a new processor based off of an <b>exact</b> copy of the copyFromProcessor.
     * Note that we are using the {@link org.lisapark.octopus.core.Reproducible} interface on {@link Input}s,
     * {@link Parameter}s and {@link Output} if there is one.
     *
     * @param copyFromProcessor that we are getting copies from
     */
    protected Processor(Processor<MEMORY_TYPE> copyFromProcessor) {
        super(copyFromProcessor);

        for (Input input : copyFromProcessor.getInputs()) {
            this.addInput(input.copyOf());
        }

        this.setOutput(copyFromProcessor.getOutput().copyOf());
    }


    protected void addInput(Input.Builder input) {
        addInput(input.build());
    }

    protected void addInput(Input input) {
        this.inputs.add(input);
    }

    protected void setOutput(Output.Builder output) {
        setOutput(output.build());
    }

    protected void setOutput(Output output) {
        this.output = output;

        this.outputEventType = new EventType();
        this.outputEventType.addAttribute(output.getAttribute());
    }

    public Output getOutput() {
        return output;
    }

    public void setOutputAttributeName(String name) {
        output.setAttributeName(name);
    }

    public String getOutputAttributeName() {
        return output.getAttributeName();
    }

    public boolean generatesOutput() {
        return outputEventType != null;
    }

    public boolean hasInputs() {
        return !inputs.isEmpty();
    }

    @Override
    public EventType getOutputEventType() {
        return outputEventType;
    }

    public List<Input> getInputs() {
        return ImmutableList.copyOf(inputs);
    }

    /**
     * The {@link org.lisapark.octopus.core.processor.Processor} will validate it's {@link #parameters}, {@link #inputs},
     * and {@link #output} in that order. Any subclass that wants to do cross parameter validation should override
     * this method to do so.
     *
     * @throws ValidationException if there is a validation problem
     */
    @Override
    public void validate() throws ValidationException {
        super.validate();

        for (Input input : inputs) {
            input.validate();
        }

        if (output != null) {
            output.validate();
        }
    }

    /**
     * Subclasses need to implement this method to return a <b>new</b> {@link Processor} based on this one.
     *
     * @return new processor
     */
    public abstract Processor<MEMORY_TYPE> newInstance();

    public abstract Processor<MEMORY_TYPE> copyOf();

    public abstract CompiledProcessor<MEMORY_TYPE> compile();

    public Memory<MEMORY_TYPE> createMemoryForProcessor(MemoryProvider memoryProvider) {
        return null;
    }
}
