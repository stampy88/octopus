package org.lisapark.octopus.designer.canvas.actions;

import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.canvas.ProcessingScene;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

import java.awt.event.KeyEvent;

/**
 * This action responds to the user pressing the {@link KeyEvent#VK_DELETE} while a {@link Widget} is selected
 * in the scene. It will verify that the object for the widget is a {@link ExternalSource} then call the
 * {@link ProcessingScene#removeExternalSource(org.lisapark.octopus.core.source.external.ExternalSource)} method.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class RemoveExternalSourceAction extends WidgetAction.Adapter {
    private final ProcessingScene processingScene;

    public RemoveExternalSourceAction(ProcessingScene processingScene) {

        this.processingScene = processingScene;
    }

    public State keyTyped(Widget widget, WidgetKeyEvent event) {
        if (event.getKeyChar() == KeyEvent.VK_DELETE) {
            Object object = processingScene.findObject(widget);

            if (object != null && object instanceof ExternalSource) {
                ExternalSource externalSource = (ExternalSource) object;

                processingScene.removeExternalSource(externalSource);
                return State.CONSUMED;
            }
        }
        return State.REJECTED;
    }
}
