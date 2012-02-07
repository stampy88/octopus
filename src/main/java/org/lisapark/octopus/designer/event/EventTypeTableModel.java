package org.lisapark.octopus.designer.event;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.EditorContext;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;

import javax.swing.table.AbstractTableModel;

/**
 * This is the Swing {@link javax.swing.table.TableModel} that is used for displaying attributes of an {@link org.lisapark.octopus.core.event.EventType}
 */
class EventTypeTableModel extends AbstractTableModel implements ContextSensitiveTableModel {

    static final int ATTRIBUTE_NAME_COLUMN = 0;
    static final int ATTRIBUTE_TYPE_COLUMN = 1;

    private EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        if (column == ATTRIBUTE_NAME_COLUMN) {
            return "Attribute Name";
        } else {
            return "Attribute Type";
        }
    }

    @Override
    public int getRowCount() {
        return eventType.getNumberOfAttributes();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Attribute attribute = eventType.getAttributeAt(rowIndex);

        if (columnIndex == ATTRIBUTE_NAME_COLUMN) {
            attribute.setName((String) value);
        } else {
            attribute.setType((Class) value);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Attribute attribute = eventType.getAttributeAt(rowIndex);

        if (columnIndex == ATTRIBUTE_NAME_COLUMN) {
            return attribute.getName();
        } else {
            return attribute.getType();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public ConverterContext getConverterContextAt(int row, int column) {
        return null;
    }

    @Override
    public EditorContext getEditorContextAt(int row, int column) {
        return null;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == ATTRIBUTE_NAME_COLUMN) {
            return String.class;
        } else {
            return Class.class;
        }
    }

    @Override
    public Class<?> getCellClassAt(int row, int column) {
        if (column == ATTRIBUTE_NAME_COLUMN) {
            return String.class;
        } else {
            return Class.class;
        }
    }
}
