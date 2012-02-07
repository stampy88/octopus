package org.lisapark.octopus.core.parameter;

import org.lisapark.octopus.core.Persistable;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public class ShortParameter extends Parameter<Short> {

    protected ShortParameter(Builder<Short> builder) {
        super(builder);
    }

    protected ShortParameter(ShortParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public String getValueForDisplay() {
        return String.valueOf(getValue());
    }

    @Override
    public Parameter<Short> copyOf() {
        return new ShortParameter(this);
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
