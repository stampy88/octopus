package org.lisapark.octopus.core;

import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.processor.ProcessorComponent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Output<T> extends ProcessorComponent {

    private final Attribute<T> attribute;

    //private final EventType eventType;

    private Output(Builder<T> builder) {
        super(builder.id, builder.name, builder.description);
        this.attribute = builder.attribute;

//        this.eventType = new EventType();
//        if(attribute != null) {
//            this.eventType.addAttribute(attribute);
//        }
    }

    private Output(Output<T> existingOutput, ReproductionMode mode) {
        super(existingOutput);

        if (mode == ReproductionMode.NEW_INSTANCE) {
            this.attribute = existingOutput.attribute.newInstance();
            // this.eventType = existingOutput.eventType.newInstance();
        } else {
            this.attribute = existingOutput.attribute.copyOf();
            // this.eventType = existingOutput.eventType.copyOf();
        }
    }

    public void setAttributeName(String name) {
        attribute.setName(name);
    }

    public String getAttributeName() {
        return attribute.getName();
    }

    public Attribute<T> getAttribute() {
        return attribute;
    }

    @Override
    public Output<T> newInstance() {
        return new Output<T>(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Output<T> copyOf() {
        return new Output<T>(this, ReproductionMode.COPY_OF);
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
