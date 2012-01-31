package org.lisapark.octopus.designer.properties;

import org.lisapark.octopus.core.Input;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class InputProperty extends ComponentProperty<Input> {
    InputProperty(Input input) {
        super(input);
        setType(String.class);
        setCategory("Inputs");
        setEditable(false);
    }

    @Override
    public Object getValue() {
        Input input = getComponent();
        return input.getSource() == null ? "" : input.getSource().getOutput().getName();
    }

    public void setValue(Object value) {
        // noop
    }
}
