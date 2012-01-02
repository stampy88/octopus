package org.lisapark.octopus.core.processor;

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.memory.MemoryProvider;
import org.lisapark.octopus.core.processor.parameter.Parameter;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * This {@link Processor} is used for computing a Simple Moving Average on a single input and producing an average
 * as the output. A simple moving average is formed by computing the average price of a number over a specific
 * number of periods.
 * <p/>
 * For example, most moving averages are based on closing prices. A 5-day simple moving average is the five
 * day sum of closing prices divided by five. As its name implies, a moving average is an average that moves.
 * Old data is dropped as new data comes available. This causes the average to move along the time scale.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Sma extends Processor<Double> {
    private static final String DEFAULT_NAME = "SMA";
    private static final String DEFAULT_DESCRIPTION = "Simple Moving Average";

    /**
     * Sma takes only a single parameter, namely how long the time scale or window is. This is the identifier of the
     * parameter.
     */
    private static final int WINDOW_LENGTH_PARAMETER_ID = 1;

    /**
     * Sma takes a single input
     */
    private static final int INPUT_ID = 1;
    private static final int OUTPUT_ID = 1;

    protected Sma(UUID id, String name, String description) {
        super(id, name, description);
    }

    protected Sma(UUID id, Sma copyFromSma) {
        super(id, copyFromSma);
    }

    protected Sma(Sma copyFromSma) {
        super(copyFromSma);
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

    @Override
    public Sma newInstance() {
        return new Sma(UUID.randomUUID(), this);
    }

    @Override
    public Sma copyOf() {
        return new Sma(this);
    }

    /**
     * {@link Sma}s need memory to store the prior events that will be used to calculate the average based on. We
     * used a {@link MemoryProvider#createCircularBuffer(int)} to store this data.
     *
     * @param memoryProvider used to create sma's memory
     * @return circular buffer
     */
    @Override
    public Memory<Double> createMemoryForProcessor(MemoryProvider memoryProvider) {
        return memoryProvider.createCircularBuffer(getWindowLength());
    }

    /**
     * Validates and compile this Sma. Doing so takes a "snapshot" of the {@link #inputs} and {@link #output}
     * and returns a {@link CompiledProcessor}.
     *
     * @return CompiledProcessor
     */
    public CompiledProcessor<Double> compile() throws ValidationException {
        validate();

        // we copy all the inputs and output taking a "snapshot" of this processor so we are isolated of changes
        Sma copy = copyOf();
        return new CompiledSma(copy);
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
        sma.addParameter(
                Parameter.integerParameterWithIdAndName(WINDOW_LENGTH_PARAMETER_ID, "Window Length").defaultValue(10).required(true)
        );

        // only a single double input
        sma.addInput(
                Input.doubleInputWithId(INPUT_ID).name("Input").description("Input for SMA")
        );
        // double output
        sma.setOutput(
                Output.doubleOutputWithId(OUTPUT_ID).nameAndDescription("Moving Average").attributeName("average")
        );

        return sma;
    }

    /**
     * This {@link CompiledProcessor} is the actual logic that implements the Simple Moving Average.
     */
    static class CompiledSma extends CompiledProcessor<Double> {
        private final String inputAttributeName;

        protected CompiledSma(Sma sma) {
            super(sma);
            this.inputAttributeName = sma.getInput().getSourceAttributeName();
        }

        @Override
        public Object processEvent(Memory<Double> memory, Map<Integer, Event> eventsByInputId) {
            // sma only has a single event
            Event event = eventsByInputId.get(INPUT_ID);

            Double newItem = event.getAttributeAsDouble(inputAttributeName);

            if (newItem == null) {
                newItem = 0D;
            }
            memory.add(newItem);

            double total = 0;
            long numberItems = 0;
            final Collection<Double> memoryItems = memory.values();

            for (Double memoryItem : memoryItems) {
                total += memoryItem;
                numberItems++;
            }

            return total / numberItems;
        }
    }
}
