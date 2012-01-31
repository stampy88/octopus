package org.lisapark.octopus.designer.canvas;

import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingSceneConnectProvider implements ConnectProvider {

    private ProcessingScene scene;

    public ProcessingSceneConnectProvider(ProcessingScene scene) {
        this.scene = scene;
    }

    private OutputPin getOutPinForFromWidget(Widget widget) {
        Object object = scene.findObject(widget);

        OutputPin sourcePin = null;
        if (object instanceof OutputPin) {
            sourcePin = (OutputPin) object;
        }

        return sourcePin;
    }

    private InputPin getInputPinFromWidget(Widget widget) {
        Object object = scene.findObject(widget);

        InputPin inputPin = null;
        if (object instanceof InputPin) {
            inputPin = (InputPin) object;
        }

        return inputPin;
    }

    public boolean isSourceWidget(Widget sourceWidget) {

        return getOutPinForFromWidget(sourceWidget) != null;
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        Object object = scene.findObject(targetWidget);

        ConnectorState connectorState = ConnectorState.REJECT;

        if (object instanceof InputPin) {
            connectorState = ConnectorState.ACCEPT;

        } else if (object != null) {
            connectorState = ConnectorState.REJECT_AND_STOP;
        }

        return connectorState;
    }

    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {

        return null;
    }

    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        if (isSourceWidget(sourceWidget) && isTargetWidget(sourceWidget, targetWidget).equals(ConnectorState.ACCEPT)) {
            // we create a connection from the Source's output pin to the target's input pin
            OutputPin outputPin = getOutPinForFromWidget(sourceWidget);
            InputPin inputPin = getInputPinFromWidget(targetWidget);

            scene.connectOutputPinToInputPin(outputPin, inputPin);
        }
    }
}
