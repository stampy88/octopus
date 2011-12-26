package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.Reproducible;
import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.event.Attribute;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.source.Source;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Input<T> extends ProcessorComponent implements Reproducible {

    private final Class<T> type;

    private Source source;
    private Attribute sourceAttribute;

    private Input(Builder<T> builder) {
        super(builder.id, builder.name, builder.description);
        this.type = builder.type;
    }

    private Input(Input<T> copyFromInput) {
        super(copyFromInput);
        this.type = copyFromInput.type;

        if (copyFromInput.source != null) {
            Source newSource = (Source) copyFromInput.source.newInstance();
            Attribute newSourceAttribute = (copyFromInput.sourceAttribute == null) ? null : copyFromInput.sourceAttribute.newInstance();

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
            throw new ValidationException(String.format("Cannot set the source attribute before setting the source"));
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
        return new Input<T>(this);
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
