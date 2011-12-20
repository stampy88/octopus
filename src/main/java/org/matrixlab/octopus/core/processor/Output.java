package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.Reproducible;
import org.matrixlab.octopus.core.event.Attribute;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Output<T> implements Reproducible {

    private final Attribute<T> attribute;
    private String displayName;
    private String description;

    private Output(Builder<T> builder) {
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.attribute = builder.attribute;
    }

    private Output(Output<T> existingOutput) {
        this.displayName = existingOutput.displayName;
        this.description = existingOutput.description;
        this.attribute = existingOutput.attribute.newInstance();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAttributeName(String name) {
        attribute.setName(name);
    }

    public String getAttributeName() {
        return attribute.getName();
    }

    Attribute<T> getAttribute() {
        return attribute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Output<T> newInstance() {
        return new Output<T>(this);
    }

    public static Builder<String> stringOutput() {
        return new Builder<String>(String.class);
    }

    public static Builder<Boolean> booleanOutput() {
        return new Builder<Boolean>(Boolean.class);
    }

    public static Builder<Long> longOutput() {
        return new Builder<Long>(Long.class);
    }

    public static Builder<Short> shortOutput() {
        return new Builder<Short>(Short.class);
    }

    public static Builder<Integer> integerOutput() {
        return new Builder<Integer>(Integer.class);
    }

    public static Builder<Double> doubleOutput() {
        return new Builder<Double>(Double.class);
    }

    public static Builder<Float> floatOutput() {
        return new Builder<Float>(Float.class);
    }

    public static class Builder<T> {
        private String displayName;
        private String description;
        private Class<T> type;
        private String attributeName;
        private Attribute<T> attribute;

        private Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<T> displayName(String name) {
            this.displayName = name;
            return this;
        }

        public Builder<T> displayNameAndDescription(String name) {
            this.displayName = name;
            this.description = name;
            return this;
        }

        public Builder<T> attributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public Builder<T> description(String description) {
            this.description = description;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Output<T> build() {
            attribute = (Attribute<T>) Attribute.newAttribute(type, attributeName);
            return new Output<T>(this);
        }
    }
}
