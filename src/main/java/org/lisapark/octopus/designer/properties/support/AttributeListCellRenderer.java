package org.lisapark.octopus.designer.properties.support;

import org.lisapark.octopus.core.event.Attribute;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class AttributeListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Attribute attribute = (Attribute) value;

        if (attribute != null) {
            value = attribute.getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
