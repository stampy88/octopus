package org.lisapark.octopus.core.processor;

import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.event.Attribute;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessorOutput extends Output {

    private final Attribute<?> attribute;

    protected ProcessorOutput(ProcessorOutput existingOutput) {
        super(existingOutput);

        this.attribute = existingOutput.attribute.copyOf();
    }

    public ProcessorOutput(Builder builder) {
        super(builder.id, builder.name, builder.description);
        this.attribute = builder.attribute;

        // add the output to the event
        addAttribute(builder.attribute);
    }

    public Class<?> getAttributeType() {
        return attribute.getType();
    }

    public void setAttributeName(String name) {
        attribute.setName(name);
    }

    public String getAttributeName() {
        return attribute.getName();
    }

    @Override
    public ProcessorOutput copyOf() {
        return new ProcessorOutput(this);
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
        public ProcessorOutput build() {
            checkState(attributeName != null, "attributeName is required");
            attribute = (Attribute<T>) Attribute.newAttribute(type, attributeName);
            return new ProcessorOutput(this);
        }
    }
}
