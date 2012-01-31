package org.lisapark.octopus.designer.event;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.EditorContext;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.designer.properties.PropertiesPresentationModel;
import org.lisapark.octopus.util.Naming;

import javax.swing.table.AbstractTableModel;

/**
 * This is the presentation model for an {@link EventType}. This model pulls the state and behavior of the view out
 * into a model class that is part of the presentation. The Presentation Model coordinates with the domain layer
 * and provides an interface to the view that minimizes decision making in the view. The view either stores all i
 * ts state in the Presentation Model.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventTypePresentationModel {

    private static final int EMPTY_SELECTION = -1;

    static final int ATTRIBUTE_NAME_COLUMN = 0;
    static final int ATTRIBUTE_TYPE_COLUMN = 1;

    /**
     * Note that this name doesn't have any spaces in it so that it conforms the naming rules
     *
     * @see org.lisapark.octopus.util.Naming#checkValidity(String, String)
     */
    private static final String DEFAULT_ATTR_NAME_PREFIX = "attribute_";

    private int selectedRow = EMPTY_SELECTION;
    private Class currentAttributeClass;

    /**
     * This is the current event type we are supporting.
     */
    private EventType eventType;

    /**
     * This is our parent presentation model that is used for checking if an attribute is in use.
     */
    private final PropertiesPresentationModel propertiesPresentationModel;

    /**
     * The table model used for displaying the {@link Attribute}s of an {@link EventType}
     */
    private final EventTypeTableModel tableModel;

    public EventTypePresentationModel(PropertiesPresentationModel propertiesPresentationModel) {
        this.propertiesPresentationModel = propertiesPresentationModel;
        this.tableModel = new EventTypeTableModel();
    }

    String getTitle() {
        return "Octopus";
    }

    String getInformationText() {
        return "Enter in the attribute names and types for this event definition. Please note that changing the" +
                " attribute types or removing attributes may affect already connected processors or sinks.";
    }

    String getChangeAttributeTypeWarningText() {
        String text = "<html>The attribute '%s' is currently in use. Changing the type of the attribute <i>may</i> invalidate its usages.<br>" +
                "<br>Are you sure you want to change the type?</html>";

        if (selectedRow != EMPTY_SELECTION) {
            Attribute currentAttribute = eventType.getAttributeAt(selectedRow);

            text = String.format(text, currentAttribute.getName());
        }
        return text;
    }

    String getRemoveAttributeWarningText() {
        String text = "<html>The attribute '%s' is currently in use. Removing the the attribute <b>will</b> invalidate its usages.<br>" +
                "<br>Are you sure you want to remove the attribute?</html>";

        if (selectedRow != EMPTY_SELECTION) {
            Attribute currentAttribute = eventType.getAttributeAt(selectedRow);

            text = String.format(text, currentAttribute.getName());
        }
        return text;
    }

    void validateAttributeName(String name) throws ValidationException {
        Naming.checkValidity(name, "Attribute name");
    }

    EventTypeTableModel getTableModel() {
        return tableModel;
    }

    Class[] getAllowedAttributeClasses() {
        return Attribute.SUPPORTED_TYPES;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        tableModel.fireTableDataChanged();
    }

    Class getCurrentAttributeClass() {
        return currentAttributeClass;
    }

    void setCurrentAttributeClass(Class currentAttributeClass) {
        this.currentAttributeClass = currentAttributeClass;
    }

    void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    void removeCurrentAttribute() {
        if (selectedRow != EMPTY_SELECTION) {
            if (selectedRow < eventType.getNumberOfAttributes()) {
                eventType.removeAttributeAt(selectedRow);

                tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
            }

            this.selectedRow = EMPTY_SELECTION;
        }
    }

    void addNewAttribute() {
        String name = determineNewAttributeName();
        Attribute newAttribute = Attribute.stringAttribute(name);

        eventType.addAttribute(newAttribute);
        tableModel.fireTableRowsInserted(eventType.getNumberOfAttributes() - 1, eventType.getNumberOfAttributes());
    }

    String determineNewAttributeName() {
        String newName = DEFAULT_ATTR_NAME_PREFIX + "1";

        int suffix = 2;
        while (eventType.containsAttributeWithName(newName)) {
            newName = DEFAULT_ATTR_NAME_PREFIX + suffix;
            suffix++;
        }

        return newName;
    }

    boolean isCurrentAttributeInUse() {
        boolean inUse = false;
        if (selectedRow != EMPTY_SELECTION) {
            Attribute currentAttribute = eventType.getAttributeAt(selectedRow);

            inUse = propertiesPresentationModel.isAttributeInUse(currentAttribute);
        }
        return inUse;
    }

    /**
     * This is the Swing {@link javax.swing.table.TableModel} that is used for displaying attributes of an {@link EventType}
     */
    private class EventTypeTableModel extends AbstractTableModel implements ContextSensitiveTableModel {

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
}
