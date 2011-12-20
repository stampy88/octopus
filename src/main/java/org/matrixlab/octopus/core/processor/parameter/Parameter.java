package org.matrixlab.octopus.core.processor.parameter;

import org.matrixlab.octopus.core.Reproducible;
import org.matrixlab.octopus.core.ValidationException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link Parameter} is an instance of a configurable parameter of a {@link org.matrixlab.octopus.core.processor.Processor}. They have a {@link #name} that
 * can be displayed in a user interface along with the {@link #description}. These can be tailored specifically
 * to the user's likes. They can also be configured as a {@link #required} parameter in which case said parameter
 * is not valid unless the user enters a value. Lastly, the parameter can be configured with a {@link #constraint} in
 * order to restrict allowed values for this parameter.
 * <p/>
 * They are generally created via the a static builder method or the {@link #newInstance()} in which case they based
 * on an existing {@link Parameter}.
 * <p/>
 * Subclasses need to implement the {@link #parseValueFromString(String)} to convert the specified string to the type
 * of this parameter.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Parameter<T> implements Reproducible {

    /**
     * The name of the parameter - can be overridden by the user.
     */
    private String name;

    /**
     * More human friendly description of the name
     */
    private String description;

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
        checkArgument(builder != null, "builder cannot be null");
        this.name = builder.name;
        this.description = builder.description;
        this.required = builder.required;
        this.value = builder.defaultValue;
        this.constraint = builder.constraint;
    }

    /**
     * Copy constructor for creating a new {@link Parameter} based off the copyFromParameter. Note that we use the
     * {@link org.matrixlab.octopus.core.Reproducible#newInstance()} for the {@link #constraint}
     *
     * @param copyFromParameter to get values
     */
    protected Parameter(Parameter<T> copyFromParameter) {
        this.name = copyFromParameter.name;
        this.description = copyFromParameter.description;
        this.value = copyFromParameter.value;
        this.required = copyFromParameter.required;

        if (copyFromParameter.constraint != null) {
            this.constraint = (Constraint<T>) copyFromParameter.constraint.newInstance();
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

    public String getName() {
        return name;
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
        validate(name, value);

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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Parameter that = (Parameter) other;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + " = " + getValue();
    }

    public static Builder<String> stringParameter(String name) {
        return new Builder<String>(name, String.class);
    }

    public static Builder<Boolean> booleanParameter(String name) {
        return new Builder<Boolean>(name, Boolean.class);
    }

    public static Builder<Long> longParameter(String name) {
        return new Builder<Long>(name, Long.class);
    }

    public static Builder<Integer> integerParameter(String name) {
        return new Builder<Integer>(name, Integer.class);
    }

    public static Builder<Float> floatParameter(String name) {
        return new Builder<Float>(name, Float.class);
    }

    public static Builder<Double> doubleParameter(String name) {
        return new Builder<Double>(name, Double.class);
    }

    /**
     * Builder class for creating parameters in a DSL like fashion
     *
     * @param <T> type of parameter
     */
    public static class Builder<T> {
        private String name;
        private String description;
        private final Class<T> type;
        private T defaultValue;
        private boolean required;
        private Constraint<T> constraint;

        private Builder(String name, Class<T> type) {
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
