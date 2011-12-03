package org.matrixlab.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class IntegerParameter extends Parameter<Integer> {

    protected IntegerParameter(Builder<Integer> builder) {
        super(builder);
    }

    protected IntegerParameter(IntegerParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public Parameter<Integer> newInstance() {
        return new IntegerParameter(this);
    }

    @Override
    public Integer parseValueFromString(String stringValue) throws ConversionException {
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(String.format("Could not convert %s into a number", stringValue));
        }
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
