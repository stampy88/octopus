package org.matrixlab.octopus.core;

import org.matrixlab.octopus.core.event.Event;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalEventSource extends EventSource {

    Event readEvent();
}
