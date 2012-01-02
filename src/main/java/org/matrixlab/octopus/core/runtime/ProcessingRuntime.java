package org.matrixlab.octopus.core.runtime;

import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.source.Source;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ProcessingRuntime {
    void start();

    void sendEventFromSource(Event event, Source source);
}
