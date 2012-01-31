package org.lisapark.octopus.designer;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class OctopusIconsFactory {

    public static final String OCTOPUS_LARGE = "/octopus-icon.png";

    public static ImageIcon getImageIcon(String name) {
        if (name != null) {
            return IconsFactory.getImageIcon(OctopusIconsFactory.class, name);
        } else {
            return null;
        }
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(OctopusIconsFactory.class);
    }
}
