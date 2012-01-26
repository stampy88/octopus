package org.lisapark.octopus.view.designer.actions;

import org.lisapark.octopus.view.designer.Connection;
import org.lisapark.octopus.view.designer.ProcessingScene;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

import java.awt.event.KeyEvent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class RemoveConnectionAction extends WidgetAction.Adapter {
    private final ProcessingScene processingScene;

    public RemoveConnectionAction(ProcessingScene processingScene) {

        this.processingScene = processingScene;
    }

    public State keyTyped(Widget widget, WidgetKeyEvent event) {
        if (event.getKeyChar() == KeyEvent.VK_DELETE) {
            Object object = processingScene.findObject(widget);

            if (object != null && object instanceof Connection) {
                Connection connection = (Connection) object;

                // clear the source of the connection
                connection.clearSource();

                processingScene.removeEdge(connection);
                return State.CONSUMED;
            }
        }
        return State.REJECTED;
    }
}
