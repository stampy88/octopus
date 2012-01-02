package org.lisapark.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class FloatParameter extends Parameter<Float> {

    protected FloatParameter(Builder<Float> builder) {
        super(builder);
    }

    protected FloatParameter(FloatParameter existingParameter, ReproductionMode mode) {
        super(existingParameter, mode);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public Parameter<Float> newInstance() {
        return new FloatParameter(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Parameter<Float> copyOf() {
        return new FloatParameter(this, ReproductionMode.COPY_OF);
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
