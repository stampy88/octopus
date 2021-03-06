package org.lisapark.octopus.designer.properties;

import org.lisapark.octopus.core.AbstractComponent;
import org.lisapark.octopus.swing.table.BaseProperty;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
abstract class ComponentProperty<T extends AbstractComponent> extends BaseProperty {

    private T component;

    protected ComponentProperty(T component) {
        this.component = component;
        setName(component.getName());
        setDescription(component.getDescription());
    }

    protected T getComponent() {
        return component;
    }

    @Override
    public String getToolTipText() {
        return component.getDescription();
    }
}
