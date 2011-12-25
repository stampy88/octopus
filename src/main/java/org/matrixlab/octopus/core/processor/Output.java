package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.Reproducible;
import org.matrixlab.octopus.core.event.Attribute;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Output<T> extends ProcessorComponent implements Reproducible {

    private final Attribute<T> attribute;

    private Output(Builder<T> builder) {
        super(builder.id, builder.name, builder.description);
        this.attribute = builder.attribute;
    }

    private Output(Output<T> existingOutput) {
        super(existingOutput);
        this.attribute = existingOutput.attribute.newInstance();
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

    public Output<T> newInstance() {
        return new Output<T>(this);
    }

    public static Builder<String> stringOutputWithId(int id) {
        return new Builder<String>(id, String.class);
    }

    public static Builder<Boolean> booleanOutputWithId(int id) {
        return new Builder<Boolean>(id, Boolean.class);
    }

    public static Builder<Long> longOutputWithId(int id) {
        return new Builder<Long>(id, Long.class);
    }

    public static Builder<Short> shortOutputWithId(int id) {
        return new Builder<Short>(id, Short.class);
    }

    public static Builder<Integer> integerOutputWithId(int id) {
        return new Builder<Integer>(id, Integer.class);
    }

    public static Builder<Double> doubleOutputWithId(int id) {
        return new Builder<Double>(id, Double.class);
    }

    public static Builder<Float> floatOutputWithId(int id) {
        return new Builder<Float>(id, Float.class);
    }

    public static class Builder<T> {
        private final int id;
        private String name;
        private String description;
        private Class<T> type;
        private String attributeName;
        private Attribute<T> attribute;

        private Builder(int id, Class<T> type) {
            this.id = id;
            this.type = type;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> nameAndDescription(String name) {
            this.name = name;
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
