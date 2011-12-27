package org.matrixlab.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ShortParameter extends Parameter<Short> {

    protected ShortParameter(Builder<Short> builder) {
        super(builder);
    }

    protected ShortParameter(ShortParameter existingParameter, ReproductionMode mode) {
        super(existingParameter, mode);
    }

    @Override
    public Parameter<Short> newInstance() {
        return new ShortParameter(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Parameter<Short> copyOf() {
        return new ShortParameter(this, ReproductionMode.COPY_OF);
    }

    @Override
    public Short parseValueFromString(String stringValue) throws ConversionException {
        try {
            return Short.parseShort(stringValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(String.format("Could not convert %s into a number", stringValue));
        }
    }

    @Override
    public Class<Short> getType() {
        return Short.class;
    }


}
