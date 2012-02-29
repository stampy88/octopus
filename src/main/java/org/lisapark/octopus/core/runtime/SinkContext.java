package org.lisapark.octopus.core.runtime;

import java.io.PrintStream;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface SinkContext {

    PrintStream getStandardOut();

    PrintStream getStandardError();
}
