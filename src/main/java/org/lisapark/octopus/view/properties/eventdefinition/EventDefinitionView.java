package org.lisapark.octopus.view.properties.eventdefinition;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.grid.ContextSensitiveTable;
import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.ListComboBoxCellEditor;
import com.jidesoft.grid.StringCellEditor;
import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.validation.ValidationObject;
import com.jidesoft.validation.ValidationResult;
import com.jidesoft.validation.Validator;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.swing.DefaultValidationFailedListener;
import org.lisapark.octopus.swing.EnhancedContextSensitiveTable;
import org.lisapark.octopus.util.Naming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventDefinitionView extends PopupPanel {
    private static final Logger LOG = LoggerFactory.getLogger(EventDefinitionView.class);

    private EventDefinitionTableModel model = new EventDefinitionTableModel();

    public EventDefinitionView(String paramString) {
        JideTable table = createTextArea();
        JideScrollPane localJScrollPane = new JideScrollPane(table);
        //localJScrollPane.setVerticalScrollBarPolicy(22);
        //localJScrollPane.setAutoscrolls(true);
        localJScrollPane.setPreferredSize(new Dimension(300, 200));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        setLayout(new BorderLayout());
        add(localJScrollPane, "Center");
        setTitle(paramString);
        setDefaultFocusComponent(table);
    }

    public Object getSelectedObject() {
        return model.getEventType();
    }

    public void setSelectedObject(Object selectedObject) {
        LOG.debug("Editing event type {}", selectedObject);
        if (selectedObject != null) {
            model.setEventType((EventType) selectedObject);
        }
    }

    protected ContextSensitiveTable createTextArea() {
        EnhancedContextSensitiveTable table = new EnhancedContextSensitiveTable(model);
        table.setValidationFailedListener(new DefaultValidationFailedListener(this));
        //
        StringCellEditor stringCellEditor = new StringCellEditor();
        stringCellEditor.addValidationListener(new AttributeNameValidator());
        table.setDefaultEditor(Class.class, new ListComboBoxCellEditor(Attribute.SUPPORTED_TYPES));
        table.setDefaultEditor(String.class, stringCellEditor);

        return table;
    }


    private class AttributeNameValidator implements Validator {

        @Override
        public ValidationResult validating(ValidationObject validationObject) {
            // start off initially as valid, that is what true is here
            ValidationResult result = new ValidationResult(true);
            // if it isn't valid we want to have the cell editor stay right where it is
            result.setFailBehavior(ValidationResult.FAIL_BEHAVIOR_PERSIST);

            Object value = validationObject.getNewValue();

            try {
                Naming.checkValidity((String) value, "Attribute name");
            } catch (ValidationException e) {
                result.setValid(false);
                result.setMessage(e.getLocalizedMessage());
            }

            return result;
        }
    }

    private class AttributeTypeEditor extends ListComboBoxCellEditor {

    }
}

