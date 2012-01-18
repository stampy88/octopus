package org.lisapark.octopus.core.processor;

import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.memory.Memory;

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

    protected Addition(Addition additionToCopy) {
        super(additionToCopy);
    }

    public ProcessorInput getFirstInput() {
        // there is only one input for an Sma
        return getInputs().get(0);
    }

    public ProcessorInput getSecondInput() {
        // there is only one input for an Sma
        return getInputs().get(1);
    }

    @Override
    public Addition newInstance() {
        return new Addition(UUID.randomUUID(), this);
    }

    @Override
    public Addition copyOf() {
        return new Addition(this);
    }

    @Override
    public CompiledProcessor<Void> compile() throws ValidationException {
        validate();

        // we copy all the inputs and output taking a "snapshot" of this processor so we are isolated of changes
        Addition copy = copyOf();

        return new CompiledAddition(copy);
    }

    /**
     * Returns a new {@link Addition} processor configured with all the appropriate
     * {@link org.lisapark.octopus.core.parameter.Parameter}s, {@link org.lisapark.octopus.core.Input}s and {@link org.lisapark.octopus.core.Output}.
     *
     * @return new {@link Addition}
     */
    public static Addition newTemplate() {
        UUID processorId = UUID.randomUUID();
        Addition addition = new Addition(processorId, DEFAULT_NAME, DEFAULT_DESCRIPTION);

        // two double inputs
        addition.addInput(ProcessorInput.doubleInputWithId(FIRST_INPUT_ID).name("First Operand").description("First operand for addition"));
        addition.addInput(ProcessorInput.doubleInputWithId(SECOND_INPUT_ID).name("Second Operand").description("Second operand for addition"));

        // double output
        addition.setOutput(ProcessorOutput.doubleOutputWithId(OUTPUT_ID).nameAndDescription("Total").attributeName("sum"));

        return addition;
    }

    // todo need a join

    static class CompiledAddition extends CompiledProcessor<Void> {
        private final String firstAttributeName;
        private final String secondAttributeName;

        protected CompiledAddition(Addition addition) {
            super(addition);

            firstAttributeName = addition.getFirstInput().getSourceAttributeName();
            secondAttributeName = addition.getSecondInput().getSourceAttributeName();
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