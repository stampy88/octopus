package org.lisapark.octopus.designer.properties;

import com.google.common.collect.Lists;
import com.jidesoft.grid.AbstractPropertyTableModel;
import com.jidesoft.grid.Property;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.parameter.Parameter;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.event.EventTypePresentationModel;
import org.lisapark.octopus.swing.EnhancedProperty;

import java.util.Collection;
import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesPresentationModel {

    private Node currentNode;

    private PropertyTableModel tableModel;

    private EventTypePresentationModel eventTypePresentationModel;

    public PropertiesPresentationModel() {
        tableModel = new PropertyTableModel();
        eventTypePresentationModel = new EventTypePresentationModel(this);
    }

    public PropertyTableModel getTableModel() {
        return tableModel;
    }

    public boolean isAttributeInUse(Attribute attribute) {
        return false;
    }

    public void setCurrentNode(Object data) {
        currentNode = (Node) data;
        if (currentNode instanceof Processor) {
            setCurrentNode((Processor<?>) currentNode);

        } else if (currentNode instanceof ExternalSource) {
            setCurrentNode((ExternalSource) currentNode);

        } else if (currentNode instanceof ExternalSink) {
            setCurrentNode((ExternalSink) currentNode);

        } else {
            throw new IllegalArgumentException(String.format("%s is not a valid data type for properties model", data));
        }
    }

    public void setCurrentNode(Processor<?> processor) {
        List<EnhancedProperty> newProperties = Lists.newArrayList();

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

        tableModel.setProperties(newProperties);
    }

    public void setCurrentNode(ExternalSink externalSink) {
        List<EnhancedProperty> newProperties = Lists.newArrayList();

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

        tableModel.setProperties(newProperties);
    }

    public void setCurrentNode(ExternalSource externalSource) {
        List<EnhancedProperty> newProperties = Lists.newArrayList();

        if (externalSource != null) {
            Collection<Parameter> parameters = externalSource.getParameters();
            for (Parameter parameter : parameters) {
                newProperties.add(new ParameterProperty(parameter));
            }

            // update the current event type on the presentation model
            eventTypePresentationModel.setEventType(externalSource.getOutput().getEventType());

            newProperties.add(new OutputProperty(externalSource.getOutput(), eventTypePresentationModel));
        }

        tableModel.setProperties(newProperties);
    }

    class PropertyTableModel extends AbstractPropertyTableModel<Property> {

        private List<EnhancedProperty> properties = Lists.newArrayList();

        private void setProperties(List<EnhancedProperty> newProperties) {
            List<Property> originalProperties = getOriginalProperties();

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
        private void removePropertyChangeListeners(List<Property> properties) {
            for (Property property : properties) {
                property.removePropertyChangeListener(this);

                if (property.hasChildren()) {

                    removePropertyChangeListeners((List<Property>) property.getChildren());
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
             * We need a null check here because this method is called by {@link AbstractPropertyTableModel}'s
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
