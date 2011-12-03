package org.matrixlab.octopus.core.external;

import org.matrixlab.octopus.core.Source;
import org.matrixlab.octopus.core.event.Event;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalSource extends Source {

    Event readEvent();
}

