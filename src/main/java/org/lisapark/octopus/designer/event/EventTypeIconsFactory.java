package org.lisapark.octopus.designer.event;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class EventTypeIconsFactory {

    public static final String ADD = "icons/add.png";
    public static final String DELETE = "icons/delete.png";

    static ImageIcon getImageIcon(String name) {
        if (name != null) {
            return IconsFactory.getImageIcon(EventTypeIconsFactory.class, name);
        } else {
            return null;
        }
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(EventTypeIconsFactory.class);
    }
}
