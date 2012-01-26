package org.lisapark.octopus.view.properties;

import com.google.common.collect.Lists;
import com.jidesoft.grid.AbstractPropertyTableModel;
import com.jidesoft.grid.Property;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.parameter.Parameter;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.swing.EnhancedProperty;

import java.util.Collection;
import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesModel extends AbstractPropertyTableModel<Property> {

    private List<EnhancedProperty> properties = Lists.newArrayList();

    public void setData(Object data) {
        if (data instanceof Processor) {
            setData((Processor<?>) data);

        } else if (data instanceof ExternalSource) {
            setData((ExternalSource) data);

        } else if (data instanceof ExternalSink) {
            setData((ExternalSink) data);

        } else {
            throw new IllegalArgumentException(String.format("%s is not a valid data type for properties model", data));
        }
    }

    public void setData(Processor<?> processor) {
        List<EnhancedProperty> newProperties = Lists.newArrayList();

        if (processor != null) {
            Collection<Parameter> parameters = processor.getParameters();
            for (Parameter parameter : parameters) {
                newProperties.add(new ParameterProperty(parameter));
            }

            // todo we need to see the sources attributes  and what about validation?
            Collection<ProcessorInput> inputs = processor.getInputs();
            for (ProcessorInput input : inputs) {
                newProperties.add(new ProcessorInputProperty(input));
            }

            newProperties.add(new ProcessorOutputProperty(processor.getOutput()));
        }

        setProperties(newProperties);
    }

    public void setData(ExternalSink externalSink) {
        List<EnhancedProperty> newProperties = Lists.newArrayList();

        if (externalSink != null) {
            Collection<Parameter> parameters = externalSink.getParameters();
            for (Parameter parameter : parameters) {
                newProperties.add(new ParameterProperty(parameter));
            }

            // todo we need to see the sources attributes  and what about validation?
            Collection<? extends Input> inputs = externalSink.getInputs();
            for (Input input : inputs) {
                newProperties.add(new InputProperty(input));
            }
        }

        setProperties(newProperties);
    }

    public void setData(ExternalSource externalSource) {
        List<EnhancedProperty> newProperties = Lists.newArrayList();

        if (externalSource != null) {
            Collection<Parameter> parameters = externalSource.getParameters();
            for (Parameter parameter : parameters) {
                newProperties.add(new ParameterProperty(parameter));
            }

            // todo we need to see the sources attributes  and what about validation?
            newProperties.add(new OutputProperty(externalSource.getOutput()));
        }

        setProperties(newProperties);
    }

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
