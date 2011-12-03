package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.event.Attribute;

import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Input<T> {

    private final Class<T> type;
    private String displayName;
    private String description;

    private Attribute sourceAttribute;

    private UUID sourceId;

    protected Input(Builder<T> builder) {
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.type = builder.type;
    }

    protected Input(Input<T> existingInput) {
        this.displayName = existingInput.displayName;
        this.description = existingInput.description;
        this.type = existingInput.type;
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

    public UUID getSourceId() {
        return sourceId;
    }

    public void setSourceAndAttribute(UUID sourceId, Attribute sourceAttribute) throws ValidationException {
        if (!sourceAttribute.isCompatibleWith(getType())) {
            throw new ValidationException(
                    String.format("The attribute '%s' is not compatible with the input '%s'", sourceAttribute,
                            getDisplayName())
            );
        }
        this.sourceId = sourceId;
        this.sourceAttribute = sourceAttribute;
    }

    public Input newInstance() {
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
