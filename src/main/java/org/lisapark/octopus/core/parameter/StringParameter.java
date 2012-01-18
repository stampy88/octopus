package org.lisapark.octopus.core.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class StringParameter extends Parameter<String> {

    protected StringParameter(Builder<String> spec) {
        super(spec);
    }

    protected StringParameter(StringParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public String getValueForDisplay() {
        return getValue();
    }

    @Override
    public Parameter<String> copyOf() {
        return new StringParameter(this);
    }

    @Override
    public String parseValueFromString(String stringValue) throws ConversionException {
        return stringValue;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
