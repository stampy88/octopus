package org.lisapark.octopus.designer.event;

import com.jidesoft.combobox.ListExComboBox;
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.JideOptionPane;
import com.jidesoft.dialog.StandardDialog;
import com.jidesoft.plaf.UIDefaultsLookup;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.OctopusIconsFactory;
import org.lisapark.octopus.swing.BaseTable;
import org.lisapark.octopus.swing.Borders;
import org.lisapark.octopus.swing.ComponentFactory;
import org.lisapark.octopus.swing.LayoutConstants;
import org.lisapark.octopus.swing.table.ComboBoxCellEditor;
import org.lisapark.octopus.swing.table.StringCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This is the dialog for displaying and interacting with an {@link EventType}. It displays a panel
 * which contains buttons for adding/removing {@link org.lisapark.octopus.core.event.Attribute}s and changing
 * said attribute's properties.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventTypeDialog extends StandardDialog {

    private static final Logger LOG = LoggerFactory.getLogger(EventTypeDialog.class);

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
    private ExternalSource externalSource;

    public EventTypeDialog(Frame frame, EventType eventType, ExternalSource externalSource, ProcessingModel processingModel) {
        super(frame);
        this.eventType = eventType;
        this.externalSource = externalSource;
        this.processingModel = processingModel;
    }

    @Override
    public JComponent createBannerPanel() {
        BannerPanel bannerPanel = new BannerPanel("Event Definition",
                "Enter in the attribute names and types for this event definition. Please note that changing the " +
                        "attribute types or removing attributes may affect already connected processors or sinks.",
                OctopusIconsFactory.getImageIcon(OctopusIconsFactory.OCTOPUS_LARGE));
        bannerPanel.setBackground(Color.WHITE);
        bannerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        return bannerPanel;
    }

    @Override
    public JComponent createContentPanel() {
        JPanel contentPanel = ComponentFactory.createPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(Borders.PADDING_BORDER);

        attributeTable = createAttributeTable();
        JScrollPane scrollPane = ComponentFactory.createScrollPaneWithComponent(attributeTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        Box buttonPanel = Box.createHorizontalBox();

        JButton addBtn = ComponentFactory.createStyledButtonWithAction(new AddAction());
        addBtn.setFocusable(false);

        JButton deleteBtn = ComponentFactory.createStyledButtonWithAction(new DeleteAction());
        deleteBtn.setFocusable(false);

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(addBtn);
        buttonPanel.add(Box.createHorizontalStrut(LayoutConstants.COMMAND_BUTTON_SPACE));
        buttonPanel.add(deleteBtn);

        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(LayoutConstants.LABEL_COMPONENT_SPACE));
        contentPanel.add(scrollPane);

        return contentPanel;
    }

    JTable createAttributeTable() {
        this.tableModel = new EventTypeTableModel(eventType);
        BaseTable table = ComponentFactory.createTableWithModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        //table.setValidationFailedListener(new DefaultValidationFailedListener(this));

        StringCellEditor stringCellEditor = new StringCellEditor();
        stringCellEditor.addValidationListener(new AttributeNameValidator());

        TableColumn nameColumn = table.getColumnModel().getColumn(EventTypeTableModel.ATTRIBUTE_NAME_COLUMN);
        nameColumn.setCellEditor(stringCellEditor);

        TableColumn typeColumn = table.getColumnModel().getColumn(EventTypeTableModel.ATTRIBUTE_TYPE_COLUMN);
        typeColumn.setCellEditor(new AttributeTypeEditor());
        typeColumn.setCellRenderer(new AttributeClassRender());

        return table;
    }

    @Override
    public ButtonPanel createButtonPanel() {
        ButtonPanel buttonPanel = new ButtonPanel();
        // note that these padding numbers coincide with what Jide recommends for StandardDialog button panels
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton okButton = ComponentFactory.createButton();
        okButton.setName(OK);
        okButton.setAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.okButtonText")) {
            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_AFFIRMED);
                setVisible(false);
                dispose();
            }
        });

        JButton cancelButton = ComponentFactory.createButton();
        cancelButton.setName(CANCEL);
        cancelButton.setAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.cancelButtonText")) {
            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_CANCELLED);
                setVisible(false);
                dispose();
            }
        });
        // we don't need to verify input because canceling the dialog reverts changes 
        cancelButton.setVerifyInputWhenFocusTarget(false);

        buttonPanel.addButton(okButton, ButtonPanel.AFFIRMATIVE_BUTTON);
        buttonPanel.addButton(cancelButton, ButtonPanel.CANCEL_BUTTON);

        setDefaultCancelAction(cancelButton.getAction());
        setDefaultAction(okButton.getAction());
        getRootPane().setDefaultButton(okButton);

        return buttonPanel;
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

            inUse = processingModel.isExternalSourceAttributeInUse(externalSource, currentAttribute);
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
                result = JideOptionPane.showConfirmDialog(EventTypeDialog.this,
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
    class AttributeTypeEditor extends ComboBoxCellEditor {

        private Class currentAttributeClass;

        public AttributeTypeEditor() {
            super(Attribute.SUPPORTED_TYPES);
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
        public boolean isEditorDoneEditing() {
            int result = JOptionPane.YES_OPTION;

            Class newClass = (Class) getCellEditorValue();
            Class oldClass = currentAttributeClass;

            LOG.debug("Trying to change attribute type from {} to {}", oldClass, newClass);

            // warn the user if they try and change an attribute that is in use
            if (!newClass.equals(oldClass) && isCurrentAttributeInUse()) {

                result = JideOptionPane.showConfirmDialog(EventTypeDialog.this,
                        getChangeAttributeTypeWarningText(),
                        TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
            }

            if (result == JOptionPane.OK_OPTION) {
                LOG.debug("Changing attribute type to {}", newClass);
            }

            return result == JOptionPane.OK_OPTION;
        }
    }

    public static EventType editEventType(Component parent, EventType eventType, ExternalSource externalSource,
                                          ProcessingModel processingModel) {
        JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
        EventTypeDialog dialog = new EventTypeDialog(frame, eventType, externalSource, processingModel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

        if (dialog.getDialogResult() == StandardDialog.RESULT_AFFIRMED) {
            return dialog.eventType;

        } else {
            return null;
        }
    }
}
