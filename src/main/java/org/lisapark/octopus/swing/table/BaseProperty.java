package org.lisapark.octopus.swing.table;

import com.jidesoft.grid.Property;

/**
 * This is a base class for all {@link Property}s in Octopus. This is an extension of a Jide property that will be
 * used to add new functionality.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class BaseProperty extends Property {

    /**
     * Returns the tool tip that should be used for this property
     *
     * @return tool tip text
     */
    public abstract String getToolTipText();
}
