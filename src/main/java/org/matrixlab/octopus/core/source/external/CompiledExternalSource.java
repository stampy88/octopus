package org.matrixlab.octopus.core.source.external;

import org.matrixlab.octopus.core.runtime.ProcessingRuntime;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface CompiledExternalSource {

    void startProcessingEvents(ProcessingRuntime runtime);

    void stopProcessingEvents();
}
