package org.lisapark.octopus.view.properties.eventdefinition;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.EditorContext;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;

import javax.swing.table.AbstractTableModel;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventDefinitionTableModel extends AbstractTableModel implements ContextSensitiveTableModel {

    private EventType eventType;

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Attribute attribute = eventType.getAttributeAt(rowIndex);

        if (columnIndex == 0) {
            attribute.setName((String) aValue);
        } else {

        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Attribute attribute = eventType.getAttributeAt(rowIndex);

        if (columnIndex == 0) {
            return attribute.getName();
        } else {
            return attribute.getType();
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public ConverterContext getConverterContextAt(int row, int column) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EditorContext getEditorContextAt(int row, int column) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        } else {
            return Class.class;
        }
    }

    @Override
    public Class<?> getCellClassAt(int row, int column) {
        if (column == 0) {
            return String.class;
        } else {
            return Class.class;
        }
    }
}
