package org.matrixlab.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class BooleanParameter extends Parameter<Boolean> {

    protected BooleanParameter(Builder<Boolean> builder) {
        super(builder);
    }

    protected BooleanParameter(BooleanParameter existingParameter) {
        super(existingParameter);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public Boolean parseValueFromString(String stringValue) throws ConversionException {
        return parseBoolean(stringValue);
    }

    @Override
    public Parameter<Boolean> newInstance() {
        return new BooleanParameter(this);
    }

    static Boolean parseBoolean(String value) throws ConversionException {
        String lowerCasedValue = value.toLowerCase();
        Boolean parsedValue = null;

        if (lowerCasedValue.equals("true") || lowerCasedValue.equals("1") ||
                lowerCasedValue.equals("on") || lowerCasedValue.equals("yes") || lowerCasedValue.equals("y")) {
            parsedValue = true;

        } else if (lowerCasedValue.equals("false") || lowerCasedValue.equals("0") ||
                lowerCasedValue.equals("off") || lowerCasedValue.equals("no") || lowerCasedValue.equals("n")) {

            parsedValue = false;
        }

        if (parsedValue == null) {
            throw new ConversionException(String.format("%s is not a valid Boolean value", value));
        }
        return parsedValue;
    }
}
