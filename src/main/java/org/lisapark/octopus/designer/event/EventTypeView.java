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
import com.jidesoft.validation.ValidationObject;
import com.jidesoft.validation.ValidationResult;
import com.jidesoft.validation.Validator;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.swing.Borders;
import org.lisapark.octopus.swing.DefaultValidationFailedListener;
import org.lisapark.octopus.swing.EnhancedContextSensitiveTable;
import org.lisapark.octopus.swing.LayoutConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This is the view for displaying and interacting with the {@link EventTypePresentationModel}. It displays a panel
 * which contains buttons for adding/removing {@link org.lisapark.octopus.core.event.Attribute}s and changing
 * said attribute's properties.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventTypeView extends PopupPanel {
    private static final Logger LOG = LoggerFactory.getLogger(EventTypeView.class);

    private JTable attributeTable;
    private final EventTypePresentationModel presentationModel;

    public EventTypeView(EventTypePresentationModel presentationModel) {

        this.presentationModel = presentationModel;
        init();
        setTitle(presentationModel.getTitle());
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(Borders.PADDING_BORDER);

        MultilineLabel informationLbl = new MultilineLabel(presentationModel.getInformationText());

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
        return presentationModel.getEventType();
    }

    public void setSelectedObject(Object eventType) {
        LOG.debug("Editing event type {}", eventType);

        if (eventType != null) {
            presentationModel.setEventType((EventType) eventType);
        }
    }

    ContextSensitiveTable createAttributeTable() {
        EnhancedContextSensitiveTable table = new EnhancedContextSensitiveTable(presentationModel.getTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setValidationFailedListener(new DefaultValidationFailedListener(this));
        table.getSelectionModel().addListSelectionListener(new CurrentAttributeListener());

        StringCellEditor stringCellEditor = new StringCellEditor();
        stringCellEditor.addValidationListener(new AttributeNameValidator());

        TableColumn nameColumn = table.getColumnModel().getColumn(EventTypePresentationModel.ATTRIBUTE_NAME_COLUMN);
        nameColumn.setCellEditor(stringCellEditor);

        TableColumn typeColumn = table.getColumnModel().getColumn(EventTypePresentationModel.ATTRIBUTE_TYPE_COLUMN);
        typeColumn.setCellEditor(new AttributeTypeEditor());
        typeColumn.setCellRenderer(new AttributeTypeRender());

        return table;
    }

    private class CurrentAttributeListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                presentationModel.setSelectedRow(attributeTable.getSelectedRow());
            }
        }
    }

    private class DeleteAction extends AbstractAction {
        private DeleteAction() {
            putValue(Action.SMALL_ICON, EventTypeIconsFactory.getImageIcon(EventTypeIconsFactory.DELETE));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.YES_OPTION;

            if (presentationModel.isCurrentAttributeInUse()) {
                result = JideOptionPane.showConfirmDialog(EventTypeView.this,
                        presentationModel.getRemoveAttributeWarningText(),
                        presentationModel.getTitle(),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
            }

            if (result == JOptionPane.YES_OPTION) {
                presentationModel.removeCurrentAttribute();
            }
        }
    }

    private class AddAction extends AbstractAction {
        private AddAction() {
            putValue(Action.SMALL_ICON, EventTypeIconsFactory.getImageIcon(EventTypeIconsFactory.ADD));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            presentationModel.addNewAttribute();
        }
    }

    private class AttributeTypeRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
                value = ((Class) value).getSimpleName();
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    /**
     * This editor is responsible for displays the different types that are allowed for an
     * {@link org.lisapark.octopus.core.event.Attribute}. It also will check to warn the user when changing the type
     * of an attriubte that is in use.
     */
    class AttributeTypeEditor extends ListComboBoxCellEditor {

        public AttributeTypeEditor() {
            super(presentationModel.getAllowedAttributeClasses());
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
            presentationModel.setCurrentAttributeClass((Class) value);

            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int result = JOptionPane.YES_OPTION;

            Class newClass = (Class) getCellEditorValue();
            Class oldClass = presentationModel.getCurrentAttributeClass();

            LOG.debug("Trying to change attribute type from {} to {}", oldClass, newClass);

            // warn the user if they try and change an attribute that is in use
            if (!newClass.equals(oldClass) && presentationModel.isCurrentAttributeInUse()) {

                result = JideOptionPane.showConfirmDialog(EventTypeView.this,
                        presentationModel.getChangeAttributeTypeWarningText(),
                        presentationModel.getTitle(),
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

    /**
     * Implementation of a Jide {@link Validator} that delegates to the {@link EventTypeView#presentationModel} to see
     * if the attribute name is valid.
     */
    private class AttributeNameValidator implements Validator {

        @Override
        public ValidationResult validating(ValidationObject validationObject) {
            // start off initially as valid, that is what true is here
            ValidationResult result = new ValidationResult(true);
            // if it isn't valid we want to have the cell editor stay right where it is
            result.setFailBehavior(ValidationResult.FAIL_BEHAVIOR_PERSIST);

            Object value = validationObject.getNewValue();

            try {
                presentationModel.validateAttributeName((String) value);
            } catch (ValidationException e) {
                result.setValid(false);
                result.setMessage(e.getLocalizedMessage());
            }

            return result;
        }
    }
}

