package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.event.Attribute;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.source.Source;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Input<T> extends ProcessorComponent {

    private final Class<T> type;

    private Source source;
    private Attribute sourceAttribute;

    private Input(Builder<T> builder) {
        super(builder.id, builder.name, builder.description);
        this.type = builder.type;
    }

    private Input(Input<T> copyFromInput, ReproductionMode mode) {
        super(copyFromInput);
        this.type = copyFromInput.type;

        if (copyFromInput.source != null) {
            Source newSource;
            Attribute newSourceAttribute;

            if (mode == ReproductionMode.NEW_INSTANCE) {
                newSource = copyFromInput.source.newInstance();
                newSourceAttribute = (copyFromInput.sourceAttribute == null) ? null : copyFromInput.sourceAttribute.newInstance();

            } else {
                newSource = copyFromInput.source.copyOf();
                newSourceAttribute = (copyFromInput.sourceAttribute == null) ? null : copyFromInput.sourceAttribute.copyOf();
            }
            this.source = newSource;
            this.sourceAttribute = newSourceAttribute;
        }
    }

    public Class<?> getType() {
        return type;
    }

    public Attribute getSourceAttribute() {
        return sourceAttribute;
    }

    public String getSourceAttributeName() {
        String name = null;

        if (sourceAttribute != null) {
            name = sourceAttribute.getName();
        }

        return name;
    }

    public Source getSource() {
        return source;
    }

    public Input<T> clearSource() {
        this.source = null;
        this.sourceAttribute = null;

        return this;
    }

    public Input<T> connectSource(Source source) {
        checkArgument(source != null, "source cannot be null");
        this.source = source;

        return this;
    }

    public void setSourceAttribute(Attribute sourceAttribute) throws ValidationException {
        if (this.source == null) {
            throw new ValidationException("Cannot set the source before setting the source attribute");
        }
        EventType sourceType = source.getOutputEventType();

        if (!sourceType.containsAttribute(sourceAttribute)) {
            throw new ValidationException(String.format("Source does not contain an attribute named '%s'", sourceAttribute));
        }

        if (!sourceAttribute.isCompatibleWith(getType())) {
            throw new ValidationException(
                    String.format("The attribute '%s' is not compatible with the input '%s'", sourceAttribute,
                            getName())
            );
        }
        this.sourceAttribute = sourceAttribute;
    }

    public Input<T> newInstance() {
        return new Input<T>(this, ReproductionMode.NEW_INSTANCE);
    }

    @Override
    public Input<T> copyOf() {
        return new Input<T>(this, ReproductionMode.COPY_OF);
    }

    /**
     * Validates the state of this input to ensure that both the {@link #source} and {@link #sourceAttribute} are
     * non-null.
     *
     * @throws ValidationException thrown if there is a problem
     */
    @Override
    public void validate() throws ValidationException {
        // just need to ensure that an source and sourceAttribute have been set
        if (this.source == null) {
            throw new ValidationException(String.format("Please set the source for input %s", getName()));
        }

        if (this.sourceAttribute == null) {
            throw new ValidationException(String.format("Please set the source attribute for input %s", getName()));
        }
    }

    public static Builder<String> stringInputWithId(int id) {
        return new Builder<String>(id, String.class);
    }

    public static Builder<Boolean> booleanInputWithId(int id) {
        return new Builder<Boolean>(id, Boolean.class);
    }

    public static Builder<Long> longInputWithId(int id) {
        return new Builder<Long>(id, Long.class);
    }

    public static Builder<Short> shortInputWithId(int id) {
        return new Builder<Short>(id, Short.class);
    }

    public static Builder<Integer> integerInputWithId(int id) {
        return new Builder<Integer>(id, Integer.class);
    }

    public static Builder<Double> doubleInputWithId(int id) {
        return new Builder<Double>(id, Double.class);
    }

    public static Builder<Float> floatInputWithId(int id) {
        return new Builder<Float>(id, Float.class);
    }

    public static class Builder<T> {
        private String name;
        private String description;
        private final int id;
        private final Class<T> type;

        private Builder(int id, Class<T> type) {
            this.id = id;
            this.type = type;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> description(String description) {
            this.description = description;
            return this;
        }

        public Input<T> build() {
            return new Input<T>(this);
        }
    }
}
