package org.lisapark.octopus.designer.properties;

import com.google.common.collect.Lists;
import com.jidesoft.action.CommandBar;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.AbstractPropertyTableModel;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.Property;
import com.jidesoft.grid.PropertyPane;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.parameter.Parameter;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.event.EventTypePopupPanel;
import org.lisapark.octopus.swing.DefaultValidationFailedListener;
import org.lisapark.octopus.swing.EnhancedProperty;
import org.lisapark.octopus.swing.EnhancedPropertyTable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesPanel extends JPanel {

    private ProcessingModel processingModel;
    private Node selectedNode;

    private EnhancedPropertyTable propertyTable;
    private EventTypePopupPanel eventTypePopupPanel;
    private PropertyTableModel tableModel;

    public PropertiesPanel() {
        super(new BorderLayout());
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

        eventTypePopupPanel = new EventTypePopupPanel();
        tableModel = new PropertyTableModel();

        propertyTable = new EnhancedPropertyTable();
        propertyTable.setModel(tableModel);
        propertyTable.setValidationFailedListener(new DefaultValidationFailedListener(this));

        return propertyTable;
    }

    public void setProcessingModel(ProcessingModel processingModel) {
        this.processingModel = processingModel;
        this.selectedNode = null;
        tableModel.clearProperties();
    }

    public void clearSelection() {
        this.selectedNode = null;
        tableModel.clearProperties();
    }

    private boolean isChangeOfSelection(Node node) {
        return selectedNode == null || (!selectedNode.equals(node));
    }

    public void setSelectedProcessor(Processor processor) {
        if (isChangeOfSelection(processor)) {
            selectedNode = processor;

            tableModel.loadPropertiesForProcessor(processor);
            propertyTable.expandFirstLevel();
        }
    }

    public void setSelectedExternalSource(ExternalSource externalSource) {
        if (isChangeOfSelection(externalSource)) {
            selectedNode = externalSource;

            // update the external source and processing model on the event type panel
            eventTypePopupPanel.setProcessingModel(processingModel);
            eventTypePopupPanel.setExternalSource(externalSource);

            tableModel.loadPropertiesForExternalSource(externalSource);
            propertyTable.expandFirstLevel();
        }
    }

    public void setSelectedExternalSink(ExternalSink externalSink) {
        if (isChangeOfSelection(externalSink)) {
            selectedNode = externalSink;

            tableModel.loadPropertiesForExternalSink(externalSink);
            propertyTable.expandFirstLevel();
        }
    }

    /**
     * @author dave sinclair(david.sinclair@lisa-park.com)
     */
    class PropertyTableModel extends AbstractPropertyTableModel<Property> {

        private java.util.List<EnhancedProperty> properties = Lists.newArrayList();

        private void clearProperties() {
            java.util.List<EnhancedProperty> newProperties = Lists.newLinkedList();
            setProperties(newProperties);
        }

        private void loadPropertiesForProcessor(Processor<?> processor) {
            java.util.List<EnhancedProperty> newProperties = Lists.newArrayList();

            if (processor != null) {
                Collection<Parameter> parameters = processor.getParameters();
                for (Parameter parameter : parameters) {
                    newProperties.add(new ParameterProperty(parameter));
                }

                Collection<ProcessorInput> inputs = processor.getInputs();
                for (ProcessorInput input : inputs) {
                    newProperties.add(new ProcessorInputProperty(input));
                }

                newProperties.add(new ProcessorOutputProperty(processor.getOutput()));
            }

            setProperties(newProperties);
        }

        private void loadPropertiesForExternalSink(ExternalSink externalSink) {
            java.util.List<EnhancedProperty> newProperties = Lists.newArrayList();

            if (externalSink != null) {
                Collection<Parameter> parameters = externalSink.getParameters();
                for (Parameter parameter : parameters) {
                    newProperties.add(new ParameterProperty(parameter));
                }

                Collection<? extends Input> inputs = externalSink.getInputs();
                for (Input input : inputs) {
                    newProperties.add(new InputProperty(input));
                }
            }

            setProperties(newProperties);
        }

        private void loadPropertiesForExternalSource(ExternalSource externalSource) {
            java.util.List<EnhancedProperty> newProperties = Lists.newArrayList();

            if (externalSource != null) {
                Collection<Parameter> parameters = externalSource.getParameters();
                for (Parameter parameter : parameters) {
                    newProperties.add(new ParameterProperty(parameter));
                }

                newProperties.add(new OutputProperty(externalSource.getOutput(), eventTypePopupPanel));
            }

            setProperties(newProperties);
        }


        private void setProperties(java.util.List<EnhancedProperty> newProperties) {
            java.util.List<Property> originalProperties = getOriginalProperties();

            // we need to remove property change listeners from the old properties because PropertyTableModel listens for
            // changes and if we didn't do this we would have memory leaks
            removePropertyChangeListeners(originalProperties);

            this.properties = newProperties;

            // we need to null out the original properties here, otherwise the order of the new properties can get screwed
            // up when they are displayed if some property categories overlap with the previous ones
            setOriginalProperties(null);
            // this method will add new property change listeners and setup internal data structures in the PropertyTableModel
            // and call fireTableDataChanged()
            setOriginalProperties(Lists.<Property>newArrayList(newProperties));
        }

        @SuppressWarnings("unchecked")
        private void removePropertyChangeListeners(java.util.List<Property> properties) {
            for (Property property : properties) {
                property.removePropertyChangeListener(this);

                if (property.hasChildren()) {

                    removePropertyChangeListeners((java.util.List<Property>) property.getChildren());
                }
            }
        }

        /**
         * Gets the number of properties.
         *
         * @return the number of properties
         */
        @Override
        public int getPropertyCount() {
            /**
             * We need a null check here because this method is called by {@link com.jidesoft.grid.AbstractPropertyTableModel}'s
             * constructor before our {@link #properties} field is set
             */
            return properties == null ? 0 : properties.size();
        }

        /**
         * Gets the property at the index.
         *
         * @param index - the index
         * @return the property at the index.
         */
        @Override
        public Property getProperty(int index) {
            return properties.get(index);
        }
    }
}