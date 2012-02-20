package org.lisapark.octopus.core.runtime;

import java.io.PrintStream;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public interface SinkContext {

    PrintStream getStandardOut();

    PrintStream getStandardError();
}
