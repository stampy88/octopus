package org.matrixlab.octopus.core.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.matrixlab.octopus.core.AbstractNode;
import org.matrixlab.octopus.core.Sink;
import org.matrixlab.octopus.core.Source;
import org.matrixlab.octopus.core.compiler.CompilerContext;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.processor.parameter.Parameter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A {@link Processor} is a program unit that has one or more {@link Input}s and potentially produces an {@link Output}.
 * In addition to {@link Input}s and an {@link Output}, a processor can be configured with additional {@link Parameter}s
 * that affect the behavior of the processor.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.matrixlab.octopus.core.processor.Input
 * @see org.matrixlab.octopus.core.processor.Output
 * @see org.matrixlab.octopus.core.processor.parameter.Parameter
 */
public abstract class Processor extends AbstractNode implements Source, Sink {

    private final UUID id;
    private Map<Integer, Parameter> parametersById = Maps.newHashMap();

    /**
     * A processor will be given zero or more inputs in order to perform its processing; this will be the
     * list of all of these inputs.
     */
    private List<Input> inputs = Lists.newLinkedList();

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
        super(name, description);
        this.id = id;
    }

    /**
     * Copy constructor for creating a new processor based off of the copyFromProcessor. Note that we are using the
     * {@link org.matrixlab.octopus.core.Reproducible} interface on {@link Input}s, {@link Parameter}s and
     * {@link Output} if there is one.
     *
     * @param id                of new processor
     * @param copyFromProcessor that we are getting copies from
     */
    protected Processor(UUID id, Processor copyFromProcessor) {
        super(copyFromProcessor.getName(), copyFromProcessor.getDescription());
        this.id = id;

        for (Map.Entry<Integer, Parameter> idToParameter : copyFromProcessor.getParameters().entrySet()) {
            this.addParameter(idToParameter.getKey(), idToParameter.getValue().newInstance());
        }

        for (Input input : copyFromProcessor.getInputs()) {
            this.addInput(input.newInstance());
        }

        this.setOutput(copyFromProcessor.getOutput().newInstance());
    }

    protected void addParameter(Integer parameterId, Parameter parameter) {
        this.parametersById.put(parameterId, parameter);
    }

    protected Parameter getParameter(Integer parameterId) {
        return parametersById.get(parameterId);
    }

    protected Map<Integer, Parameter> getParameters() {
        return Maps.newHashMap(this.parametersById);
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

        this.outputEventType = new EventType(id);
        this.outputEventType.addAttribute(output.getAttribute());
    }

    public Output getOutput() {
        return output;
    }

    public UUID getId() {
        return id;
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

    public abstract <T, CONTEXT extends CompilerContext> T compile(org.matrixlab.octopus.core.compiler.Compiler<T, CONTEXT> compiler, CONTEXT context);
}
