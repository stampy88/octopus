package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.compiler.CompilerContext;
import org.matrixlab.octopus.core.processor.parameter.Parameter;

import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Sma extends Processor {
    private static final String DEFAULT_NAME = "SMA";
    private static final String DEFAULT_DESCRIPTION = "Simple Moving Average";

    private static final int WINDOW_LENGTH_PARAMETER_ID = 1;

    protected Sma(UUID id, String name, String description) {
        super(id, name, description);
    }

    public int getWindowLength() {
        return getParameter(WINDOW_LENGTH_PARAMETER_ID).getValueAsInteger();
    }

    @SuppressWarnings("unchecked")
    public void setWindowLength(int windowLength) {
        getParameter(WINDOW_LENGTH_PARAMETER_ID).setValue(windowLength);
    }

    public Input getInput() {
        // there is only one input for an Sma
        return getInputs().get(0);
    }

    public <T, CONTEXT extends CompilerContext> T compile(org.matrixlab.octopus.core.compiler.Compiler<T, CONTEXT> compiler, CONTEXT context) {
        return compiler.compile(context, this);
    }


    @Override
    public Processor newInstance() {
        UUID processorId = UUID.randomUUID();
        Sma sma = new Sma(processorId, this.getName(), this.getDescription());

        for (Map.Entry<Integer, Parameter> idToParameter : this.getParameters().entrySet()) {
            sma.addParameter(idToParameter.getKey(), idToParameter.getValue().newInstance());
        }

        for (Input input : this.getInputs()) {
            sma.addInput(input.newInstance());
        }

        sma.setOutput(this.getOutput().newInstance());

        return sma;
    }

    /**
     * Returns a new {@link Sma} processor configured with all the appropriate {@link Parameter}s, {@link Input}s
     * and {@link Output}.
     *
     * @return new {@link Sma}
     */
    public static Sma newTemplate() {
        UUID processorId = UUID.randomUUID();
        Sma sma = new Sma(processorId, DEFAULT_NAME, DEFAULT_DESCRIPTION);

        // sma only has window length paramater
        sma.addParameter(WINDOW_LENGTH_PARAMETER_ID,
                Parameter.integerParameter("Window Length").defaultValue(10).required(true).build()
        );

        // only a single double input
        sma.addInput(Input.doubleInput().displayName("Input").description("Input for SMA"));
        // double output
        sma.setOutput(Output.doubleOutput().displayNameAndDescription("Moving Average").attributeName("average"));

        return sma;
    }
}
