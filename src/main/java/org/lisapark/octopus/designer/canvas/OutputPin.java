package org.lisapark.octopus.designer.canvas;

import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.source.Source;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class OutputPin extends Pin {
    private final Source source;

    public OutputPin(Source source) {

        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public Output getOutput() {

        return source.getOutput();
    }

    @Override
    public String getName() {
        return "output???";
    }
}
