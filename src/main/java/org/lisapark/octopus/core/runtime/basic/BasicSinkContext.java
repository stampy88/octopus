package org.lisapark.octopus.core.runtime.basic;

import org.lisapark.octopus.core.runtime.SinkContext;

import java.io.PrintStream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public class BasicSinkContext implements SinkContext {

    private final PrintStream standardOut;
    private final PrintStream standardError;

    public BasicSinkContext(PrintStream standardOut, PrintStream standardError) {
        checkArgument(standardOut != null, "standardOut cannot be null");
        checkArgument(standardError != null, "standardError cannot be null");
        this.standardOut = standardOut;
        this.standardError = standardError;
    }

    @Override
    public PrintStream getStandardOut() {
        return standardOut;
    }

    @Override
    public PrintStream getStandardError() {
        return standardError;
    }
}
