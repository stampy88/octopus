package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.Reproducible;
import org.matrixlab.octopus.core.Source;
import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.event.Attribute;
import org.matrixlab.octopus.core.event.EventType;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Input<T> implements Reproducible {

    private final Class<T> type;
    private String displayName;
    private String description;

    private Source source;
    private Attribute sourceAttribute;

    private Input(Builder<T> builder) {
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.type = builder.type;
    }

    private Input(Input<T> copyFromInput) {
        this.displayName = copyFromInput.displayName;
        this.description = copyFromInput.description;
        this.type = copyFromInput.type;

        if (copyFromInput.source != null) {
            Source newSource = (Source) copyFromInput.source.newInstance();
            Attribute newSourceAttribute = (copyFromInput.sourceAttribute == null) ? null : copyFromInput.sourceAttribute.newInstance();

            this.source = newSource;
            this.sourceAttribute = newSourceAttribute;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                            getDisplayName())
            );
        }
        this.sourceAttribute = sourceAttribute;
    }

    public Input<T> newInstance() {
        return new Input<T>(this);
    }

    public static Builder<String> stringInput() {
        return new Builder<String>(String.class);
    }

    public static Builder<Boolean> booleanInput() {
        return new Builder<Boolean>(Boolean.class);
    }

    public static Builder<Long> longInput() {
        return new Builder<Long>(Long.class);
    }

    public static Builder<Short> shortInput() {
        return new Builder<Short>(Short.class);
    }

    public static Builder<Integer> integerInput() {
        return new Builder<Integer>(Integer.class);
    }

    public static Builder<Double> doubleInput() {
        return new Builder<Double>(Double.class);
    }

    public static Builder<Float> floatInput() {
        return new Builder<Float>(Float.class);
    }

    public static class Builder<T> {
        private String displayName;
        private String description;
        private final Class<T> type;

        private Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<T> displayName(String displayName) {
            this.displayName = displayName;
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
