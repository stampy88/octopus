package org.lisapark.octopus.designer.properties;

import com.jidesoft.action.CommandBar;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import org.lisapark.octopus.swing.DefaultValidationFailedListener;
import org.lisapark.octopus.swing.EnhancedPropertyTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesView extends JPanel {

    private EnhancedPropertyTable propertyTable;
    private final PropertiesPresentationModel model;

    public PropertiesView(PropertiesPresentationModel model) {
        super(new BorderLayout());
        this.model = model;
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

        final EnhancedPropertyTable enhancedPropertyTable = new EnhancedPropertyTable();
        enhancedPropertyTable.setModel(model.getTableModel());
        enhancedPropertyTable.setValidationFailedListener(new DefaultValidationFailedListener(this));
        enhancedPropertyTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.ALL_COLUMNS) {
                    enhancedPropertyTable.expandFirstLevel();
                }
            }
        });

        return enhancedPropertyTable;
    }

    PropertyTable getPropertyTable() {
        return propertyTable;
    }
}
