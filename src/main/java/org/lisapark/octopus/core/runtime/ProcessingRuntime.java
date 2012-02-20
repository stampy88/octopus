package org.lisapark.octopus.core.runtime;

import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.source.Source;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ProcessingRuntime {
    void start();

    void shutdown();

    void sendEventFromSource(Event event, Source source);
}
