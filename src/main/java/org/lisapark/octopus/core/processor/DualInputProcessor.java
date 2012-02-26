package org.lisapark.octopus.core.processor;

import com.google.common.collect.ImmutableList;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.source.Source;

import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public abstract class DualInputProcessor<MEMORY_TYPE> extends Processor<MEMORY_TYPE> {

    private ProcessorInput firstInput;
    private ProcessorInput secondInput;
    private Join join;

    protected DualInputProcessor(UUID id, String name, String description) {
        super(id, name, description);
        join = new Join();
    }

    protected DualInputProcessor(UUID id, DualInputProcessor<MEMORY_TYPE> copyFromProcessor) {
        super(id, copyFromProcessor);
        setFirstInput(copyFromProcessor.getFirstInput().copyOf());
        setSecondInput(copyFromProcessor.getSecondInput().copyOf());
        setJoin(new Join(copyFromProcessor));
    }

    protected DualInputProcessor(DualInputProcessor<MEMORY_TYPE> copyFromProcessor) {
        super(copyFromProcessor);
        setFirstInput(copyFromProcessor.getFirstInput().copyOf());
        setSecondInput(copyFromProcessor.getSecondInput().copyOf());
        setJoin(new Join(copyFromProcessor));
    }

    public ProcessorInput getFirstInput() {
        return firstInput;
    }

    public ProcessorInput getSecondInput() {
        return secondInput;
    }

    public Join getJoin() {
        return join;
    }

    protected void setJoin(Join join) {
        this.join = join;
    }

    @Override
    public List<ProcessorInput> getInputs() {
        return ImmutableList.of(firstInput, secondInput);
    }

    /**
     * We need to override the {@link Processor#isConnectedTo(Source, Attribute)} to check whether the attribute
     * is in use on the {@link #join}
     *
     * @param source    to check if it is in use by this processor
     * @param attribute to check if it is in use by this processor
     * @return true if the source and attribute is in use
     */
    @Override
    public boolean isConnectedTo(Source source, Attribute attribute) {
        boolean connected = super.isConnectedTo(source, attribute);

        if (!connected) {
            connected = join.isConnectedTo(source, attribute);
        }

        return connected;
    }

    /**
     * We need to override the {@link Processor#disconnect(Source)} to also remove the source from the {@link #join}
     *
     * @param source to disconnect
     */
    @Override
    public void disconnect(Source source) {
        super.disconnect(source);

        join.clear();
    }

    protected void setFirstInput(ProcessorInput.Builder firstInput) {
        setFirstInput(firstInput.build());
    }

    protected void setFirstInput(ProcessorInput firstInput) {
        this.firstInput = firstInput;
    }

    protected void setSecondInput(ProcessorInput.Builder secondInput) {
        setSecondInput(secondInput.build());
    }

    protected void setSecondInput(ProcessorInput secondInput) {
        this.secondInput = secondInput;
    }

    public class Join {
        private Attribute firstInputAttribute;
        private Attribute secondInputAttribute;

        private Join() {
        }

        private Join(DualInputProcessor copyFromProcssor) {
            Join copyFromJoin = copyFromProcssor.join;
            if (copyFromJoin.firstInputAttribute != null) {
                // note that this copy HAS to happen after the firstInput has been copied
                this.firstInputAttribute = firstInput.getSource().getOutput().getAttributeByName(copyFromJoin.firstInputAttribute.getName());
            }
            if (copyFromJoin.secondInputAttribute != null) {
                // note that this copy HAS to happen after the secondInput has been copied
                this.secondInputAttribute = secondInput.getSource().getOutput().getAttributeByName(copyFromJoin.secondInputAttribute.getName());
            }
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

        public boolean requiresJoin() {
            return firstInput != null && secondInput != null && !firstInput.equals(secondInput);
        }

        public void clearJoinAttributeForInput(ProcessorInput input) {
            checkArgument(input != null, "input cannot be null");

            if (input == firstInput) {
                firstInputAttribute = null;

            } else if (input == secondInput) {
                secondInputAttribute = null;

            } else {
                throw new IllegalArgumentException(input + " is not an input of this processor " + DualInputProcessor.this);
            }
        }

        public Attribute getJoinAttributeForInput(ProcessorInput input) {
            checkArgument(input != null, "input cannot be null");

            if (input == firstInput) {
                return firstInputAttribute;

            } else if (input == secondInput) {
                return secondInputAttribute;

            } else {
                throw new IllegalArgumentException(input + " is not an input of this processor " + DualInputProcessor.this);
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
}
