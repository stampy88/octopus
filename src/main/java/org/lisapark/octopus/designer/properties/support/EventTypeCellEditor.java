package org.lisapark.octopus.designer.properties.support;

import com.jidesoft.grid.ContextSensitiveCellEditor;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.event.EventTypeDialog;
import org.lisapark.octopus.swing.ComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventTypeCellEditor extends ContextSensitiveCellEditor implements ActionListener {

    private JButton button;
    private EventType eventType;
    private ProcessingModel processingModel;
    private ExternalSource externalSource;

    public EventTypeCellEditor() {
        this.button = ComponentFactory.createButton();
        this.button.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        eventType = (EventType) value;

        button.setText(eventType.toString());

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return eventType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EventType editedType = EventTypeDialog.editEventType(button, eventType, externalSource, processingModel);

        if (editedType != null) {
            this.eventType = editedType;
            fireEditingStopped();

        } else {
            fireEditingCanceled();
        }
    }

    public void setProcessingModel(ProcessingModel processingModel) {
        this.processingModel = processingModel;
    }

    public void setExternalSource(ExternalSource externalSource) {
        this.externalSource = externalSource;
    }
}
