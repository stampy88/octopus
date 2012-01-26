package org.lisapark.octopus.view.designer;

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.SeparatorWidget;

import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessorWidget extends NodeWidget {

    private LabelWidget header;
    private SeparatorWidget pinsSeparator;

    /**
     * Creates a new widget which will be used in a specified scene.
     *
     * @param scene the scene where the widget is going to be used
     */
    public ProcessorWidget(Scene scene) {
        super(scene);
        setOpaque(false);
        setLayout(LayoutFactory.createVerticalFlowLayout());
        setMinimumSize(new Dimension(128, 8));

        header = new LabelWidget(scene);
        addChild(header);

        pinsSeparator = new SeparatorWidget(scene, SeparatorWidget.Orientation.HORIZONTAL);
        addChild(pinsSeparator);
    }
}
