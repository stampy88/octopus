package org.matrixlab.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class LongParameter extends Parameter<Long> {

    protected LongParameter(Builder<Long> builder) {
        super(builder);
    }

    protected LongParameter(LongParameter existingParameter, ReproductionMode mode) {
        super(existingParameter, mode);
    }

    @Override
    public Parameter<Long> newInstance() {
        return new LongParameter(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Parameter<Long> copyOf() {
        return new LongParameter(this, ReproductionMode.COPY_OF);
    }

    @Override
    public Long parseValueFromString(String stringValue) throws ConversionException {
        try {
            return Long.parseLong(stringValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(String.format("Could not convert %s into a number", stringValue));
        }
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }


}
