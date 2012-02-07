package org.lisapark.octopus.designer.canvas;

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.source.Source;

/**
 * A connection is used in the {@link org.lisapark.octopus.designer.canvas.ProcessingScene} to connect
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Connection {

    private Input destination;

    private Connection(Source source, Input destination) {
        this.destination = destination;
        destination.connectSource(source);
    }

    public void clearSource() {
        destination.clearSource();
    }

    public static Connection connectSourceToSinkInput(Source source, Input input) {
        return new Connection(source, input);
    }
}
