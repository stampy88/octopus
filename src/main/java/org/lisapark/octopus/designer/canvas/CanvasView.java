package org.lisapark.octopus.designer.canvas;

import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.swing.LayoutConstants;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class CanvasView extends JPanel {

    private ProcessingScene scene;
    private JComponent sceneView;

    public CanvasView(ProcessingModel model) {
        super(new BorderLayout(LayoutConstants.COMPONENT_HORIZONTAL_GAP, LayoutConstants.COMPONENT_VERTICAL_GAP));

        scene = new ProcessingScene(model);
        sceneView = scene.createView();

        add(sceneView);
    }

    public void addObjectSceneListener(ObjectSceneListener listener, ObjectSceneEventType... types) {
        scene.addObjectSceneListener(listener, types);
    }

    public void modelToView(ProcessingModel model) {
        // todo dispose of old scene??

        scene = new ProcessingScene(model);
        sceneView = scene.createView();
    }
}
