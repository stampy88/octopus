package org.lisapark.octopus.designer.event;

import com.jidesoft.combobox.ListExComboBox;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.dialog.JideOptionPane;
import com.jidesoft.grid.ContextSensitiveTable;
import com.jidesoft.grid.ListComboBoxCellEditor;
import com.jidesoft.grid.StringCellEditor;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.swing.MultilineLabel;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.swing.Borders;
import org.lisapark.octopus.swing.DefaultValidationFailedListener;
import org.lisapark.octopus.swing.EnhancedContextSensitiveTable;
import org.lisapark.octopus.swing.LayoutConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This is the view for displaying and interacting with an {@link EventType}. It displays a panel
 * which contains buttons for adding/removing {@link org.lisapark.octopus.core.event.Attribute}s and changing
 * said attribute's properties.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventTypePopupPanel extends PopupPanel {
    private static final Logger LOG = LoggerFactory.getLogger(EventTypePopupPanel.class);

    private static final String TITLE = "Octopus";

    /**
     * Note that this name doesn't have any spaces in it so that it conforms the naming rules
     *
     * @see org.lisapark.octopus.util.Naming#checkValidity(String, String)
     */
    private static final String DEFAULT_ATTR_NAME_PREFIX = "attribute_";

    private static final int EMPTY_SELECTION = -1;

    private JTable attributeTable;
    private EventTypeTableModel tableModel;

    private EventType eventType;
    private ProcessingModel processingModel;

    public EventTypePopupPanel() {
        init();
        setTitle(TITLE);
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(Borders.PADDING_BORDER);

        MultilineLabel informationLbl = new MultilineLabel("Enter in the attribute names and types for this " +
                "event definition. Please note that changing the attribute types or removing attributes may " +
                "affect already connected processors or sinks.");

        attributeTable = createAttributeTable();
        JideScrollPane scrollPane = new JideScrollPane(attributeTable);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        add(informationLbl);
        add(Box.createVerticalStrut(LayoutConstants.LABEL_COMPONENT_SPACE));
        add(createButtonPanel());
        add(Box.createVerticalStrut(LayoutConstants.LABEL_COMPONENT_SPACE));
        add(scrollPane);

        setDefaultFocusComponent(attributeTable);
    }

    private JComponent createButtonPanel() {
        Box buttonPanel = Box.createHorizontalBox();

        JideButton addBtn = new JideButton(new AddAction());
        addBtn.setFocusable(false);

        JideButton deleteBtn = new JideButton(new DeleteAction());
        deleteBtn.setFocusable(false);

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(addBtn);
        buttonPanel.add(Box.createHorizontalStrut(LayoutConstants.COMMAND_BUTTON_SPACE));
        buttonPanel.add(deleteBtn);

        return buttonPanel;
    }

    /**
     * We don't support the reset button on the event definition panel
     *
     * @return false
     */
    public boolean isResetButtonVisible() {
        return false;
    }

    public Object getSelectedObject() {
        return eventType;
    }

    public void setSelectedObject(Object eventType) {
        LOG.debug("Editing event type {}", eventType);

        if (eventType != null) {
            setEventType((EventType) eventType);
        }
    }

    ContextSensitiveTable createAttributeTable() {
        this.tableModel = new EventTypeTableModel();
        EnhancedContextSensitiveTable table = new EnhancedContextSensitiveTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setValidationFailedListener(new DefaultValidationFailedListener(this));

        StringCellEditor stringCellEditor = new StringCellEditor();
        stringCellEditor.addValidationListener(new AttributeNameValidator());

        TableColumn nameColumn = table.getColumnModel().getColumn(EventTypeTableModel.ATTRIBUTE_NAME_COLUMN);
        nameColumn.setCellEditor(stringCellEditor);

        TableColumn typeColumn = table.getColumnModel().getColumn(EventTypeTableModel.ATTRIBUTE_TYPE_COLUMN);
        typeColumn.setCellEditor(new AttributeTypeEditor());
        typeColumn.setCellRenderer(new AttributeClassRender());

        return table;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        tableModel.setEventType(this.eventType);
    }

    String getChangeAttributeTypeWarningText() {
        String text = "<html>The attribute '%s' is currently in use. Changing the type of the attribute <i>may</i> invalidate its usages.<br>" +
                "<br>Are you sure you want to change the type?</html>";

        int selectedRow = attributeTable.getSelectedRow();
        if (selectedRow != EMPTY_SELECTION) {
            Attribute currentAttribute = eventType.getAttributeAt(selectedRow);

            text = String.format(text, currentAttribute.getName());
        }
        return text;
    }

    String getRemoveAttributeWarningText() {
        String text = "<html>The attribute '%s' is currently in use. Removing the the attribute <b>will</b> invalidate its usages.<br>" +
                "<br>Are you sure you want to remove the attribute?</html>";

        int selectedRow = attributeTable.getSelectedRow();
        if (selectedRow != EMPTY_SELECTION) {
            Attribute currentAttribute = eventType.getAttributeAt(selectedRow);

            text = String.format(text, currentAttribute.getName());
        }
        return text;
    }

    boolean isCurrentAttributeInUse() {
        boolean inUse = false;
        int selectedRow = attributeTable.getSelectedRow();

        if (selectedRow != EMPTY_SELECTION) {
            Attribute currentAttribute = eventType.getAttributeAt(selectedRow);

            // todo
            //inUse = processingModel.isAttributeInUse(currentAttribute);
        }
        return inUse;
    }

    private class DeleteAction extends AbstractAction {
        private DeleteAction() {
            putValue(Action.SMALL_ICON, EventTypeIconsFactory.getImageIcon(EventTypeIconsFactory.DELETE));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.YES_OPTION;

            if (isCurrentAttributeInUse()) {
                result = JideOptionPane.showConfirmDialog(EventTypePopupPanel.this,
                        getRemoveAttributeWarningText(),
                        TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
            }

            if (result == JOptionPane.YES_OPTION) {
                int selectedRow = attributeTable.getSelectedRow();

                if (selectedRow != EMPTY_SELECTION) {
                    if (selectedRow < eventType.getNumberOfAttributes()) {
                        eventType.removeAttributeAt(selectedRow);

                        tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
                    }
                }
            }
        }
    }

    private class AddAction extends AbstractAction {
        private AddAction() {
            putValue(Action.SMALL_ICON, EventTypeIconsFactory.getImageIcon(EventTypeIconsFactory.ADD));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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

    }

    /**
     * This editor is responsible for displays the different types that are allowed for an
     * {@link org.lisapark.octopus.core.event.Attribute}. It also will check to warn the user when changing the type
     * of an attriubte that is in use.
     */
    class AttributeTypeEditor extends ListComboBoxCellEditor {

        private Class currentAttributeClass;

        public AttributeTypeEditor() {
            super(Attribute.SUPPORTED_TYPES);
            setAutoStopCellEditing(false);
        }

        @Override
        protected ListExComboBox createListComboBox(ComboBoxModel model, Class<?> type) {
            ListExComboBox comboBox = super.createListComboBox(model, type);

            comboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Class currentClass = (Class) value;

                    if (currentClass != null) {
                        value = currentClass.getSimpleName();
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });

            return comboBox;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentAttributeClass = ((Class) value);

            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int result = JOptionPane.YES_OPTION;

            Class newClass = (Class) getCellEditorValue();
            Class oldClass = currentAttributeClass;

            LOG.debug("Trying to change attribute type from {} to {}", oldClass, newClass);

            // warn the user if they try and change an attribute that is in use
            if (!newClass.equals(oldClass) && isCurrentAttributeInUse()) {

                result = JideOptionPane.showConfirmDialog(EventTypePopupPanel.this,
                        getChangeAttributeTypeWarningText(),
                        TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
            }

            if (result == JOptionPane.OK_OPTION) {
                LOG.debug("Changing attribute type to {}", newClass);
                super.stopCellEditing();
            }
        }
    }

}

