package org.lisapark.octopus.view.palette;

import org.lisapark.octopus.core.Node;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class NodeListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Node node = (Node) value;
        if (node != null) {
            label.setText(node.getName());

            Icon icon = node.getIcon();
            label.setIcon(icon);
        }

        return label;
    }
}
