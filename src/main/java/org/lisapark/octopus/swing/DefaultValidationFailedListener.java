package org.lisapark.octopus.swing;

import com.jidesoft.validation.ValidationResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DefaultValidationFailedListener implements ValidationFailedListener {
    private final Component parentComponent;

    public DefaultValidationFailedListener(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void validationFailed(ValidationResult result) {
        JOptionPane.showMessageDialog(parentComponent, result.getMessage(), "Octopus", JOptionPane.ERROR_MESSAGE);
    }
}
