package org.lisapark.octopus.swing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Borders {

    /**
     * Inside border for panels
     */
    public final static Border PADDING_BORDER = BorderFactory.createEmptyBorder(
            LayoutConstants.DIALOG_MARGIN_TOP,
            LayoutConstants.DIALOG_MARGIN_LEFT,
            LayoutConstants.DIALOG_MARGIN_BOTTOM,
            LayoutConstants.DIALOG_MARGIN_RIGHT
    );

    /**
     * Inside border for panels inside of tabbed panes
     */
    public final static Border TABBED_PANE_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), PADDING_BORDER);
}
