package org.lisapark.octopus.core.runtime.basic;

import org.lisapark.octopus.core.runtime.SinkContext;

import java.io.PrintStream;

import static com.jgoodies.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public class BasicSinkContext implements SinkContext {

    private final PrintStream standardOut;

    public BasicSinkContext(PrintStream standardOut) {
        checkArgument(standardOut != null, "standardOut cannot be null");
        this.standardOut = standardOut;
    }

    @Override
    public PrintStream getStandardOut() {
        return standardOut;
    }
}
