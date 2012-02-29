package org.lisapark.octopus.core.processor;

import org.lisapark.octopus.core.Validatable;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.source.Source;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessorJoin implements Validatable {

    private final ProcessorInput firstInput;
    private final ProcessorInput secondInput;

    private Attribute firstInputAttribute;
    private Attribute secondInputAttribute;

    ProcessorJoin(ProcessorInput firstInput, ProcessorInput secondInput) {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
    }

    ProcessorJoin(ProcessorInput firstInput, Attribute firstInputAttribute, ProcessorInput secondInput, Attribute secondInputAttribute) {
        this.firstInput = firstInput;
        this.firstInputAttribute = firstInputAttribute;
        this.secondInput = secondInput;
        this.secondInputAttribute = secondInputAttribute;
    }

    public ProcessorInput getFirstInput() {
        return firstInput;
    }

    public Attribute getFirstInputAttribute() {
        return firstInputAttribute;
    }

    public String getFirstInputAttributeName() {
        return firstInputAttribute != null ? firstInputAttribute.getName() : null;
    }

    public ProcessorInput getSecondInput() {
        return secondInput;
    }

    public Attribute getSecondInputAttribute() {
        return secondInputAttribute;
    }

    public String getSecondInputAttributeName() {
        return secondInputAttribute != null ? secondInputAttribute.getName() : null;
    }

    private void clear() {
        firstInputAttribute = null;
        secondInputAttribute = null;
    }

    private boolean isConnectedTo(Source source, Attribute attribute) {
        boolean connectedTo = false;

        if (firstInput.isConnectedTo(source) && firstInputAttribute != null && firstInputAttribute.equals(attribute)) {
            connectedTo = true;

        } else if (secondInput.isConnectedTo(source) && secondInputAttribute != null && secondInputAttribute.equals(attribute)) {
            connectedTo = true;
        }

        return connectedTo;
    }

    /**
     * This method will return true if this join is required. A join is not required when the {@link #firstInput}'s
     * source and {@link #secondInput}'s source are the same. This can be the case when adding two attributes from the
     * same {@link Source} for example.
     *
     * @return true if the join is required
     */
    public boolean isRequired() {
        return firstInput != null && firstInput.getSource() != null &&
                secondInput != null && secondInput.getSource() != null &&
                !firstInput.getSource().equals(secondInput.getSource());
    }

    public void clearJoinAttributeForInput(ProcessorInput input) {
        checkArgument(input != null, "input cannot be null");

        if (input == firstInput) {
            firstInputAttribute = null;

        } else if (input == secondInput) {
            secondInputAttribute = null;

        } else {
            throw new IllegalArgumentException(input + " is not an input of this processor ");
        }
    }

    public ProcessorInput getOtherInput(ProcessorInput input) {
        checkArgument(input != null, "input cannot be null");

        if (input == firstInput) {
            return secondInput;

        } else if (input == secondInput) {
            return firstInput;

        } else {
            throw new IllegalArgumentException(input + " is not an input of this processor ");
        }
    }

    public Attribute getJoinAttributeForInput(ProcessorInput input) {
        checkArgument(input != null, "input cannot be null");

        if (input == firstInput) {
            return firstInputAttribute;

        } else if (input == secondInput) {
            return secondInputAttribute;

        } else {
            throw new IllegalArgumentException(input + " is not an input of this processor ");
        }
    }

    public void setJoinAttributeForInput(ProcessorInput input, Attribute attribute) throws ValidationException {
        checkArgument(input != null, "input cannot be null");
        checkArgument(attribute != null, "attribute cannot be null");

        if (input == firstInput) {
            setFirstInputAttribute(attribute);

        } else if (input == secondInput) {
            setSecondInputAttribute(attribute);
        } else {
            throw new IllegalArgumentException(input + " is not an input of this processor");
        }
    }


    @Override
    public void validate() throws ValidationException {
        if (firstInput.getSource() == null) {
            throw new ValidationException(
                    String.format("Please set the source for input '%s' on the join", firstInput.getName())
            );
        }
        if (secondInput.getSource() == null) {
            throw new ValidationException(
                    String.format("Please set the source for input '%s' on the join", secondInput.getName())
            );
        }
        if (isRequired() && firstInputAttribute == null) {
            throw new ValidationException(
                    String.format("Please select the attribute for input '%s' on the join", firstInput.getName())
            );
        }
        if (isRequired() && secondInputAttribute == null) {
            throw new ValidationException(
                    String.format("Please select the attribute for input '%s' on the join", secondInput.getName())
            );
        }
    }

    private void setFirstInputAttribute(Attribute firstInputAttribute) throws ValidationException {
        // if the other attribute hasn't been set, we don't need to ensure we have a valid join
        if (secondInputAttribute == null) {
            this.firstInputAttribute = firstInputAttribute;

        } else {
            if (!firstInput.isAttributeFromSource(firstInputAttribute)) {
                throw new ValidationException(
                        String.format("First Input does not contain an attribute named '%s'", firstInputAttribute.getName())
                );
            }

            if (!firstInputAttribute.isCompatibleWith(secondInputAttribute.getType())) {
                throw new ValidationException(
                        String.format("The attribute '%s' of type '%s' cannot be joined with the input '%s' of type %s",
                                firstInputAttribute, firstInputAttribute.getType().getSimpleName(),
                                secondInputAttribute.getName(), secondInputAttribute.getType().getSimpleName())
                );
            }

            this.firstInputAttribute = firstInputAttribute;
        }
    }

    private void setSecondInputAttribute(Attribute secondInputAttribute) throws ValidationException {
        // if the other attribute hasn't been set, we don't need to ensure we have a valid join
        if (firstInputAttribute == null) {
            this.secondInputAttribute = secondInputAttribute;

        } else {
            if (!secondInput.isAttributeFromSource(secondInputAttribute)) {
                throw new ValidationException(
                        String.format("Second Input does not contain an attribute named '%s'", secondInputAttribute.getName())
                );
            }
            if (!secondInputAttribute.isCompatibleWith(firstInputAttribute.getType())) {
                throw new ValidationException(
                        String.format("The attribute '%s' of type '%s' cannot be joined with the input '%s' of type %s",
                                secondInputAttribute, secondInputAttribute.getType().getSimpleName(),
                                firstInputAttribute.getName(), firstInputAttribute.getType().getSimpleName())
                );
            }

            this.secondInputAttribute = secondInputAttribute;
            // todo clean up code and what happens when they change the input?
        }
    }
}
