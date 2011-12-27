package org.matrixlab.octopus.core.processor.parameter;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class StringParameter extends Parameter<String> {

    protected StringParameter(Builder<String> spec) {
        super(spec);
    }

    protected StringParameter(StringParameter existingParameter, ReproductionMode mode) {
        super(existingParameter, mode);
    }

    @Override
    public Parameter<String> newInstance() {
        return new StringParameter(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Parameter<String> copyOf() {
        return new StringParameter(this, ReproductionMode.COPY_OF);
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
