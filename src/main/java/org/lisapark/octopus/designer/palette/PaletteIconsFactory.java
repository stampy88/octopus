package org.lisapark.octopus.designer.palette;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PaletteIconsFactory {

    public static final String PROCESSOR = "icons/function-icon.png";
    public static final String EXTERNAL_SOURCE = "icons/node-insert-icon.png";
    public static final String EXTERNAL_SINK = "icons/node-delete-child-icon.png";

    public static ImageIcon getImageIcon(String name) {
        if (name != null) {
            return IconsFactory.getImageIcon(PaletteIconsFactory.class, name);
        } else {
            return null;
        }
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(PaletteIconsFactory.class);
    }
}
