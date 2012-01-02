package org.lisapark.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DoubleParameter extends Parameter<Double> {

    protected DoubleParameter(Builder<Double> builder) {
        super(builder);
    }

    protected DoubleParameter(DoubleParameter existingParameter, ReproductionMode mode) {
        super(existingParameter, mode);
    }

    @Override
    public Parameter<Double> newInstance() {
        return new DoubleParameter(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Parameter<Double> copyOf() {
        return new DoubleParameter(this, ReproductionMode.COPY_OF);
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
