package org.matrixlab.octopus.core.source;

import org.matrixlab.octopus.core.Node;
import org.matrixlab.octopus.core.event.EventType;

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
