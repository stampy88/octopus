package org.matrixlab.octopus.core.processor.parameter;

import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.processor.ProcessorComponent;

/**
 * A {@link Parameter} is an instance of a configurable parameter of a
 * {@link org.matrixlab.octopus.core.processor.Processor}. They have a {@link #name} that can be displayed in a
 * user interface along with the {@link #description}. These can be tailored specifically to the user's likes.
 * They can also be configured as a {@link #required} parameter in which case said parameter is not valid unless
 * the user enters a value. Lastly, the parameter can be configured with a {@link #constraint} in order to restrict
 * allowed values for this parameter.
 * <p/>
 * They are generally created via the a static builder method or the {@link #newInstance()} in which case they based
 * on an existing {@link Parameter}.
 * <p/>
 * Subclasses need to implement the {@link #parseValueFromString(String)} to convert the specified string to the type
 * of this parameter.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Parameter<T> extends ProcessorComponent {

    /**
     * The actual value of the parameter
     */
    private T value;

    /**
     * True if a value is required for this parameter
     */
    private final boolean required;

    /**
     * If the value of the parameter is constrained in any way, this constraint will enforce it
     */
    private final Constraint<T> constraint;

    /**
     * Creates a new parameter according to the specified {@link Builder}
     *
     * @param builder to create parameter based off of
     */
    protected Parameter(Builder<T> builder) {
        super(builder.id, builder.name, builder.description);
        this.required = builder.required;
        this.value = builder.defaultValue;
        this.constraint = builder.constraint;
    }

    /**
     * Copy constructor for creating a new {@link Parameter} based off the copyFromParameter. Note that we use the
     * {@link org.matrixlab.octopus.core.Reproducible#newInstance()} for the {@link #constraint}
     *
     * @param copyFromParameter to get values
     * @param mode              use to tell how to create/copy {@link org.matrixlab.octopus.core.Reproducible}
     */
    @SuppressWarnings("unchecked")
    protected Parameter(Parameter<T> copyFromParameter, ReproductionMode mode) {
        super(copyFromParameter);
        this.value = copyFromParameter.value;
        this.required = copyFromParameter.required;

        if (copyFromParameter.constraint != null) {
            if (mode == ReproductionMode.NEW_INSTANCE) {
                this.constraint = copyFromParameter.constraint.newInstance();
            } else {
                this.constraint = copyFromParameter.constraint.copyOf();
            }
        } else {
            this.constraint = null;
        }
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isConstrained() {
        return constraint != null;
    }

    public String getValueAsString() {
        return (String) value;
    }

    public Integer getValueAsInteger() {
        return (Integer) value;
    }

    public Short getValueAsShort() {
        return (Short) value;
    }

    public Long getValueAsLong() {
        return (Long) value;
    }

    /**
     * This method will try and set the {@link #value} of this parameter using the specified stringValue.
     *
     * @param stringValue to set
     * @throws ValidationException thrown if the stringValue is not valid in any way
     */
    public final void setValueFromString(String stringValue) throws ValidationException {
        T value = parseValueFromString(stringValue);

        setValue(value);
    }

    /**
     * Subclasses need to implement this method to parse and return the subclass specific value from the specified
     * stringValue. If the stringValue cannot be converted, a {@link ConversionException} should be thrown.
     *
     * @param stringValue to convert
     * @return properly parsed value
     * @throws ConversionException if the stringValue cannot be converted
     */
    protected abstract T parseValueFromString(String stringValue) throws ConversionException;

    /**
     * Sets the value of this parameter.
     *
     * @param value to set
     * @throws ValidationException thrown if the value is not valid according
     */
    public final void setValue(T value) throws ValidationException {
        // ensure it is a valid according to the spec
        validate(getName(), value);

        this.value = value;
    }

    /**
     * Will try and validate the specified value according to this specification. Will throw a
     * {@link ValidationException} if the value is not valid according to it.
     *
     * @param name  of parameter - used in error message
     * @param value to validate
     * @throws ValidationException thrown if the value doesn't meet this specification
     */
    void validate(String name, T value) throws ValidationException {
        if (value == null && isRequired()) {
            throw new ValidationException(String.format("%s is a required parameter.", name));
        }

        if (constraint != null) {
            constraint.validate(name, value);
        }
    }

    /**
     * Returns the current value of this parameter
     *
     * @return value
     */
    public final T getValue() {
        return this.value;
    }

    /**
     * Validates whether the current state of this parameter is valid. If the {@link #value} of this {@link Parameter}
     * is non-null meaning that it is either the default, or it was set and validated via the {@link #setValue(Object)},
     * we no it is valid. If, however, the value is null we need to check the {@link #required} flag and throw an
     * exception if it is required.
     *
     * @throws ValidationException if there is a validation error
     */
    @Override
    public void validate() throws ValidationException {
        if (value == null && isRequired()) {
            throw new ValidationException(String.format("%s is a required parameter.", getName()));
        }

        // not null - only can be so if it is valid - so we are ok
    }

    /**
     * Subclasses need to implement this method to return the type of parameter
     *
     * @return parameter type
     */
    public abstract Class<T> getType();

    /**
     * Subclasses need to implement this method to return a <b>new</b> {@link Parameter} based on this one.
     *
     * @return new parameter
     */
    public abstract Parameter<T> newInstance();

    /**
     * Subclasses need to implement this method to return a <b>new</b> {@link Parameter} that is an <b>exact</b> copy
     * of this one.
     *
     * @return new parameter
     */
    public abstract Parameter<T> copyOf();

    @Override
    public String toString() {
        return getName() + " = " + getValue();
    }

    public static Builder<String> stringParameterWithIdAndName(int id, String name) {
        return new Builder<String>(id, name, String.class);
    }

    public static Builder<Boolean> booleanParameterWithIdAndName(int id, String name) {
        return new Builder<Boolean>(id, name, Boolean.class);
    }

    public static Builder<Long> longParameterWithIdAndName(int id, String name) {
        return new Builder<Long>(id, name, Long.class);
    }

    public static Builder<Integer> integerParameterWithIdAndName(int id, String name) {
        return new Builder<Integer>(id, name, Integer.class);
    }

    public static Builder<Float> floatParameterWithIdAndName(int id, String name) {
        return new Builder<Float>(id, name, Float.class);
    }

    public static Builder<Double> doubleParameterWithIdAndName(int id, String name) {
        return new Builder<Double>(id, name, Double.class);
    }

    /**
     * Builder class for creating parameters in a DSL like fashion
     *
     * @param <T> type of parameter
     */
    public static class Builder<T> {
        private final int id;
        private String name;
        private String description;
        private final Class<T> type;
        private T defaultValue;
        private boolean required;
        private Constraint<T> constraint;

        private Builder(int id, String name, Class<T> type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder<T> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<T> constraint(Constraint<T> constraint) {
            this.constraint = constraint;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Parameter<T> build() {
            if (type == Boolean.class) {
                return (Parameter<T>) new BooleanParameter((Builder<Boolean>) this);
            }

            if (type == Double.class) {
                return (Parameter<T>) new DoubleParameter((Builder<Double>) this);
            }

            if (type == Float.class) {
                return (Parameter<T>) new FloatParameter((Builder<Float>) this);
            }

            if (type == Integer.class) {
                return (Parameter<T>) new IntegerParameter((Builder<Integer>) this);
            }

            if (type == Long.class) {
                return (Parameter<T>) new LongParameter((Builder<Long>) this);
            }

            if (type == Short.class) {
                return (Parameter<T>) new ShortParameter((Builder<Short>) this);
            }

            if (type == String.class) {
                return (Parameter<T>) new StringParameter((Builder<String>) this);
            }

            // this should never happen
            throw new IllegalStateException("Unknown type for parameter");
        }
    }
}
