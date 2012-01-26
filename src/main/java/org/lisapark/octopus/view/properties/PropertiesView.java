package org.lisapark.octopus.view.properties;

import com.jidesoft.action.CommandBar;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import org.lisapark.octopus.swing.EnhancedPropertyTable;
import org.lisapark.octopus.swing.ValidationFailedListener;
import org.lisapark.octopus.view.Constants;
import org.lisapark.octopus.view.View;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesView extends JPanel implements View<PropertiesModel> {

    private EnhancedPropertyTable propertyTable;

    public PropertiesView() {
        super(new BorderLayout(Constants.COMPONENT_HORIZONTAL_GAP, Constants.COMPONENT_VERTICAL_GAP));
        init();
    }

    private void init() {
        propertyTable = createPropertyTable();

        PropertyPane propertyPane = new PropertyPane(propertyTable) {
            @Override
            protected JComponent createToolBarComponent() {
                CommandBar toolBar = new CommandBar();
                toolBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
                toolBar.setFloatable(false);
                toolBar.setStretch(true);
                toolBar.setPaintBackground(false);
                toolBar.setChevronAlwaysVisible(false);
                return toolBar;
            }
        };
        propertyPane.setPreferredSize(new Dimension(330, 300));
        add(propertyPane, BorderLayout.CENTER);
    }

    private EnhancedPropertyTable createPropertyTable() {
        ObjectConverterManager.initDefaultConverter();
        CellEditorManager.initDefaultEditor();

        return new EnhancedPropertyTable();
    }

    @Override
    public void modelToView(PropertiesModel model) {
        propertyTable.setModel(model);
    }

    public void setValidationFailedListener(ValidationFailedListener validationFailedListener) {
        propertyTable.setValidationFailedListener(validationFailedListener);
    }

    PropertyTable getPropertyTable() {
        return propertyTable;
    }

    @Override
    public PropertiesModel viewToModel() {
        return null;
    }
}
