package org.lisapark.octopus.swing;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ComponentValueModel;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */

/**
 * This is the base presentation model for adapting a business object to a view. The base class provides methods to
 * retrieve {@link com.jgoodies.binding.value.ValueModel}s for different properties of this model through the
 * {@link #getValueModel(String)} and {@link #getComponentValueModel(String)} methods.
 *
 * @see com.jgoodies.binding.value.ValueModel
 */
public abstract class AbstractBoundModel extends Model {

    /**
     * Returns the ValueModel for the specified property name
     *
     * @param name name of property to get model for
     * @return model for name
     */
    public final AbstractValueModel getValueModel(String name) {
        return getPresentationModel().getModel(name);
    }

    /**
     * Returns the <code>ComponentValueModel</code> for the specified property name. ValueModels are used to bind a
     * model property to a <code>JComponent<code>
     *
     * @param name name of property to get model for
     * @return model for the specified property
     */
    public final ComponentValueModel getComponentValueModel(String name) {
        return getPresentationModel().getComponentModel(name);
    }

    /**
     * Subclasses need to implement this method to return the JGoodies presentation model, which is used to get
     * {@link com.jgoodies.binding.value.ValueModel}s, for this model.
     *
     * @return JGoodies presentation model
     */
    protected abstract PresentationModel getPresentationModel();
}
