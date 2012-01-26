package org.lisapark.octopus.swing;

import com.jidesoft.grid.ContextSensitiveTable;
import com.jidesoft.grid.EditingNotStoppedException;
import com.jidesoft.validation.ValidationResult;

import javax.swing.event.ChangeEvent;
import javax.swing.table.TableModel;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EnhancedContextSensitiveTable extends ContextSensitiveTable {
    private ValidationFailedListener validationFailedListener;

    public EnhancedContextSensitiveTable() {

    }

    public EnhancedContextSensitiveTable(TableModel tableModel) {
        super(tableModel);
    }

    public ValidationFailedListener getValidationFailedListener() {
        return validationFailedListener;
    }

    public void setValidationFailedListener(ValidationFailedListener validationFailedListener) {
        this.validationFailedListener = validationFailedListener;
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        try {
            super.editingStopped(e);

        } catch (EditingNotStoppedException ex) {
            ValidationResult result = ex.getValidationResult();

            if (validationFailedListener != null) {
                validationFailedListener.validationFailed(result);
            }
        }
    }
}
