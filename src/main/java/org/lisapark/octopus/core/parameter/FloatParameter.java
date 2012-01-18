package org.lisapark.octopus.core.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class FloatParameter extends Parameter<Float> {

    protected FloatParameter(Builder<Float> builder) {
        super(builder);
    }

    protected FloatParameter(FloatParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public String getValueForDisplay() {
        return String.valueOf(getValue());
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public Parameter<Float> copyOf() {
        return new FloatParameter(this);
    }

    @Override
    public Float parseValueFromString(String stringValue) throws ConversionException {
        try {
            return Float.parseFloat(stringValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(String.format("Could not convert %s into a number", stringValue));
        }
    }
}
