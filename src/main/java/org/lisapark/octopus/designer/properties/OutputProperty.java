package org.lisapark.octopus.designer.properties;

import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.designer.properties.support.EventTypeCellEditor;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class OutputProperty extends ComponentProperty<Output> {

    OutputProperty(Output output, final EventTypeCellEditor eventTypeCellEditor) {
        super(output);

        setType(EventType.class);
        setEditable(true);
        setCategory("Output");
        setCellEditor(eventTypeCellEditor);
    }

    @Override
    public Object getValue() {
        return getComponent().getEventType();
    }

    @Override
    public void setValue(Object value) {
        getComponent().setEventType((EventType) value);
    }
}
