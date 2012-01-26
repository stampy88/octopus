package org.lisapark.octopus.view.designer;

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.sink.Sink;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class InputPin extends Pin {
    private final Sink sink;
    private final Input input;

    public InputPin(Sink sink, Input input) {

        this.sink = sink;
        this.input = input;
    }

    public Input getInput() {
        return input;
    }

    public Sink getSink() {
        return sink;
    }

    @Override
    public String getName() {
        return input.getName();
    }
}
