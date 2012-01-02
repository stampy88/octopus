package org.lisapark.octopus.core.source;

import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Source extends Node {

    // todo is EventType reproducible?

    //Output getOutput();

    /**
     * Implementers need to return the type of event this source is creating
     *
     * @return event type from this source
     */
    EventType getOutputEventType();

    //   Output getOutput();

    Source newInstance();

    Source copyOf();
}
