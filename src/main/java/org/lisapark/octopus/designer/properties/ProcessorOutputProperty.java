package org.lisapark.octopus.designer.properties;

import com.jidesoft.grid.StringCellEditor;
import com.jidesoft.validation.ValidationObject;
import com.jidesoft.validation.ValidationResult;
import com.jidesoft.validation.Validator;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.processor.ProcessorOutput;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class ProcessorOutputProperty extends ComponentProperty<ProcessorOutput> {

    ProcessorOutputProperty(ProcessorOutput output) {
        super(output);
        setType(String.class);
        setCategory("Output");

        StringCellEditor cellEditor = new StringCellEditor();
        cellEditor.addValidationListener(new ProcessorOutputValidator(output));
        setCellEditor(cellEditor);
    }

    @Override
    public Object getValue() {
        return getComponent().getAttributeName();
    }

    @SuppressWarnings("unchecked,raw")
    @Override
    public void setValue(Object value) {
        // update the value on the input
        if (value instanceof String) {
            getComponent().setAttributeName((String) value);

        } else {
            throw new IllegalArgumentException(String.format("Unknown type for input %s", getName()));
        }
    }

    /**
     * An implementation of a Jide {@link com.jidesoft.validation.Validator} that will ensure the {@link org.lisapark.octopus.core.parameter.Parameter} is valid according to
     * it's {@link org.lisapark.octopus.core.parameter.Parameter#validateValue(Object)}  method.
     */
    private static class ProcessorOutputValidator implements Validator {

        private final ProcessorOutput processorOutput;

        private ProcessorOutputValidator(ProcessorOutput processorOutput) {
            this.processorOutput = processorOutput;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ValidationResult validating(ValidationObject validationObject) {
            // start off initially as valid, that is what true is here
            ValidationResult result = new ValidationResult(true);
            // if it isn't valid we want to have the cell editor stay right where it is
            result.setFailBehavior(ValidationResult.FAIL_BEHAVIOR_PERSIST);

            Object value = validationObject.getNewValue();

            try {
                processorOutput.setAttributeName((String) value);
            } catch (ValidationException e) {
                result.setValid(false);
                result.setMessage(e.getLocalizedMessage());
            }

            return result;
        }
    }
}
