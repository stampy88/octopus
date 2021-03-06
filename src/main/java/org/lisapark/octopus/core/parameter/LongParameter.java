package org.lisapark.octopus.core.parameter;

import org.lisapark.octopus.core.Persistable;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public class LongParameter extends Parameter<Long> {

    protected LongParameter(Builder<Long> builder) {
        super(builder);
    }

    protected LongParameter(LongParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public String getValueForDisplay() {
        return String.valueOf(getValue());
    }

    @Override
    public Parameter<Long> copyOf() {
        return new LongParameter(this);
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
