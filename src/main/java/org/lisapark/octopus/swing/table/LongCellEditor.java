package org.lisapark.octopus.swing.table;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class LongCellEditor extends FormattedCellEditor {
    /**
     * Default Constructor
     */
    public LongCellEditor() {
        super(Short.class);
    }

    @Override
    protected void customizeFormattedField(JFormattedTextField field) {
        field.setHorizontalAlignment(JTextField.LEADING);

        //Set up the editor for the longs.        
        NumberFormatter longFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        field.setFormatterFactory(new DefaultFormatterFactory(longFormatter));
    }
}
