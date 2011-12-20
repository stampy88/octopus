package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.compiler.CompilerContext;

import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Addition extends Processor {
    private static final String DEFAULT_NAME = "Addition";
    private static final String DEFAULT_DESCRIPTION = "Add 2 operands";

    protected Addition(UUID id, String name, String description) {
        super(id, name, description);
    }

    protected Addition(UUID id, Addition additionToCopy) {
        super(id, additionToCopy);
    }

    public <T, CONTEXT extends CompilerContext> T compile(org.matrixlab.octopus.core.compiler.Compiler<T, CONTEXT> compiler, CONTEXT context) {
        return compiler.compile(context, this);
    }

    @Override
    public Addition newInstance() {
        return new Addition(UUID.randomUUID(), this);
    }

    /**
     * Returns a new {@link Addition} processor configured with all the appropriate
     * {@link org.matrixlab.octopus.core.processor.parameter.Parameter}s, {@link Input}s and {@link Output}.
     *
     * @return new {@link Addition}
     */
    public static Addition newTemplate() {
        UUID processorId = UUID.randomUUID();
        Addition addition = new Addition(processorId, DEFAULT_NAME, DEFAULT_DESCRIPTION);

        // two double inputs
        addition.addInput(Input.doubleInput().displayName("First Operand").description("First operand for addition"));
        addition.addInput(Input.doubleInput().displayName("Second Operand").description("Second operand for addition"));

        // double output
        addition.setOutput(Output.doubleOutput().displayNameAndDescription("Total").attributeName("sum"));

        return addition;
    }
}
