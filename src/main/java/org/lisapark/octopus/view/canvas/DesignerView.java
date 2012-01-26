package org.lisapark.octopus.view.canvas;

import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.view.Constants;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerView extends JPanel {

    private ProcessingScene scene;
    private JComponent sceneView;
    private JScrollPane sceneScrollPane;

    public DesignerView(ProcessingModel model) {
        super(new BorderLayout(Constants.COMPONENT_HORIZONTAL_GAP, Constants.COMPONENT_VERTICAL_GAP));

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

        sceneScrollPane.setViewportView(sceneView);
    }
}
