package org.lisapark.octopus.swing.table;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DoubleCellEditor extends FormattedCellEditor {
    /**
     * Default Constructor
     */
    public DoubleCellEditor() {
        super(Double.class);
    }

    @Override
    protected void customizeFormattedField(JFormattedTextField field) {
        field.setHorizontalAlignment(JTextField.LEADING);

        //Set up the editor for the doubles.
        NumberFormatter doubleFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
        field.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
    }
}
