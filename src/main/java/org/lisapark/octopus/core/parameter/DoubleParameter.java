package org.lisapark.octopus.core.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DoubleParameter extends Parameter<Double> {

    protected DoubleParameter(Builder<Double> builder) {
        super(builder);
    }

    protected DoubleParameter(DoubleParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public String getValueForDisplay() {
        return String.valueOf(getValue());
    }

    @Override
    public Parameter<Double> copyOf() {
        return new DoubleParameter(this);
    }

    @Override
    public Double parseValueFromString(String stringValue) throws ConversionException {
        try {
            return Double.parseDouble(stringValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(String.format("Could not convert %s into a number", stringValue));
        }
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
