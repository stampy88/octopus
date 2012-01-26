package org.lisapark.octopus.view.properties;

import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.grid.ExComboBoxCellEditor;
import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.swing.EnhancedProperty;
import org.lisapark.octopus.view.properties.eventdefinition.EventDefinitionView;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class OutputProperty extends EnhancedProperty {
    private final Output output;

    OutputProperty(Output output) {
        this.output = output;
        setName(output.getName() + "Event");
        setDescription(output.getDescription());
        setType(EventType.class);
        setEditable(true);
        // todo localize string
        // todo output mapping??
        setCategory("Output");
        setCellEditor(new ExComboBoxCellEditor() {
            @Override
            public ExComboBox createExComboBox() {
                return new ExComboBox(ExComboBox.DIALOG) {
                    @Override
                    public PopupPanel createPopupComponent() {
                        return new EventDefinitionView("dave");
                    }
                };
            }
        });
    }

    @Override
    public Object getValue() {

        return output.getEventType();
    }

    @Override
    public void setValue(Object value) {
        output.setEventType((EventType) value);
    }

    @Override
    public String getToolTipText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
