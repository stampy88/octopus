package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.compiler.Compiler;
import org.matrixlab.octopus.core.compiler.CompilerContext;
import org.matrixlab.octopus.core.processor.parameter.Parameter;

import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Wma extends Processor {
    private static final String DEFAULT_NAME = "WMA";
    private static final String DEFAULT_DESCRIPTION = "Weighted Moving Average";

    private static final int WINDOW_LENGTH_PARAMETER_ID = 1;

    protected Wma(UUID id, String name, String description) {
        super(id, name, description);
    }

    protected Wma(UUID id, Wma copyFromWma) {
        super(id, copyFromWma);
    }

    public int getWindowLength() {
        return getParameter(WINDOW_LENGTH_PARAMETER_ID).getValueAsInteger();
    }

    @SuppressWarnings("unchecked")
    public void setWindowLength(int windowLength) {
        getParameter(WINDOW_LENGTH_PARAMETER_ID).setValue(windowLength);
    }

    public Input getInput() {
        // there is only one input for an Wma
        return getInputs().get(0);
    }

    @Override
    public <T, CONTEXT extends CompilerContext> T compile(Compiler<T, CONTEXT> compiler, CONTEXT context) {
        return compiler.compile(context, this);
    }

    @Override
    public Wma newInstance() {
        return new Wma(UUID.randomUUID(), this);
    }

    /**
     * Returns a new {@link Wma} processor configured with all the appropriate {@link Parameter}s, {@link Input}s
     * and {@link Output}.
     *
     * @return new {@link Wma}
     */
    public static Wma newTemplate() {
        UUID processorId = UUID.randomUUID();
        Wma wma = new Wma(processorId, DEFAULT_NAME, DEFAULT_DESCRIPTION);

        // wma only has window length paramater
        wma.addParameter(WINDOW_LENGTH_PARAMETER_ID,
                Parameter.integerParameter("Window Length").defaultValue(10).build()
        );

        // only a single double input
        wma.addInput(Input.doubleInput().displayName("Input").description("Input for WMA"));
        // double output
        wma.setOutput(Output.doubleOutput().displayNameAndDescription("Weighted Moving Average").attributeName("average"));

        return wma;
    }
}
