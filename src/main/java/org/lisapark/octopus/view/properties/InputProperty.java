package org.lisapark.octopus.view.properties;

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.swing.EnhancedProperty;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class InputProperty extends EnhancedProperty {
    private final Input input;

    InputProperty(Input input) {
        this.input = input;
        setName(input.getName());
        setDescription(input.getDescription());
        setType(String.class);
        // todo localize string
        setCategory("Inputs");
        setEditable(false);
    }

    @Override
    public Object getValue() {
        return input.getSource() == null ? "" : input.getSource().getOutput().getName();
    }

    public void setValue(Object value) {
        // noop
    }

    @Override
    public String getToolTipText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
