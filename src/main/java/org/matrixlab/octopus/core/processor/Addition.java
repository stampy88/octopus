package org.matrixlab.octopus.core.processor;

import com.google.common.collect.Lists;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.memory.Memory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This {@link Processor} is used to adding two inputs together and producing an output.
 * <p/>
 * Addition is a mathematical operation that represents combining collections of objects together into a larger
 * collection. It is signified by the plus sign (+).
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Addition extends Processor<Void> {
    private static final String DEFAULT_NAME = "Addition";
    private static final String DEFAULT_DESCRIPTION = "Add 2 operands";

    /**
     * Addition takes two inputs
     */
    private static final int FIRST_INPUT_ID = 1;
    private static final int SECOND_INPUT_ID = 2;
    private static final int OUTPUT_ID = 1;

    protected Addition(UUID id, String name, String description) {
        super(id, name, description);
    }

    protected Addition(UUID id, Addition additionToCopy) {
        super(id, additionToCopy);
    }

    public Input getFirstInput() {
        // there is only one input for an Sma
        return getInputs().get(0);
    }

    public Input getSecondInput() {
        // there is only one input for an Sma
        return getInputs().get(1);
    }

    @Override
    public Addition newInstance() {
        return new Addition(UUID.randomUUID(), this);
    }

    @Override
    public CompiledProcessor<Void> compile() {
        // todo validate cross input/output/parameters here??

        // we copy all the inputs and output taking a "snapshot" of this processor so we are isolated of changes
        Input firstInputCopy = getFirstInput();
        Input secondInputCopy = getSecondInput();

        List<Input> inputs = Lists.newArrayList(firstInputCopy, secondInputCopy);
        Output outputCopy = getOutput().newInstance();

        // todo do we hide the source attribute name as input??
        return new CompiledAddition(inputs, outputCopy, getId(),
                firstInputCopy.getSourceAttributeName(), secondInputCopy.getSourceAttributeName()
        );
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
        addition.addInput(Input.doubleInputWithId(FIRST_INPUT_ID).name("First Operand").description("First operand for addition"));
        addition.addInput(Input.doubleInputWithId(SECOND_INPUT_ID).name("Second Operand").description("Second operand for addition"));

        // double output
        addition.setOutput(Output.doubleOutputWithId(OUTPUT_ID).nameAndDescription("Total").attributeName("sum"));

        return addition;
    }

    // todo need a join

    static class CompiledAddition extends CompiledProcessor<Void> {
        private final String firstAttributeName;
        private final String secondAttributeName;

        protected CompiledAddition(List<Input> inputs, Output output, UUID smaId,
                                   String firstAttributeName, String secondAttributeName) {
            super(inputs, output, smaId);
            this.firstAttributeName = firstAttributeName;
            this.secondAttributeName = secondAttributeName;
        }

        @Override
        public Object processEvent(Memory<Void> memory, Map<Integer, Event> eventsByInputId) {
            Event firstEvent = eventsByInputId.get(FIRST_INPUT_ID);
            Event secondEvent = eventsByInputId.get(SECOND_INPUT_ID);

            Double firstOperand = firstEvent.getAttributeAsDouble(firstAttributeName);
            Double secondOperand = secondEvent.getAttributeAsDouble(secondAttributeName);

            firstOperand = (firstOperand == null) ? 0 : firstOperand;
            secondOperand = (secondOperand == null) ? 0 : secondOperand;

            return firstOperand + secondOperand;
        }
    }
}
