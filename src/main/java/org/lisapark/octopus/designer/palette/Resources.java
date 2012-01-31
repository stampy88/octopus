package org.lisapark.octopus.designer.palette;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class Resources {

    static final String BASENAME = "org.lisapark.octopus.designer.palette.palette";

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}
