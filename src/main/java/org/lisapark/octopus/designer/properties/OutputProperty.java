package org.lisapark.octopus.designer.properties;

import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.grid.ExComboBoxCellEditor;
import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.designer.event.EventTypePresentationModel;
import org.lisapark.octopus.designer.event.EventTypeView;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class OutputProperty extends ComponentProperty<Output> {
    private final EventTypePresentationModel eventTypePresentationModel;

    OutputProperty(Output output, EventTypePresentationModel eventTypePresentationModel) {
        super(output);

        this.eventTypePresentationModel = eventTypePresentationModel;
        setType(EventType.class);
        setEditable(true);
        setCategory("Output");
        setCellEditor(new ExComboBoxCellEditor() {
            @Override
            public ExComboBox createExComboBox() {
                return new ExComboBox(ExComboBox.DIALOG) {
                    @Override
                    public PopupPanel createPopupComponent() {
                        return new EventTypeView(OutputProperty.this.eventTypePresentationModel);
                    }
                };
            }
        });
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
