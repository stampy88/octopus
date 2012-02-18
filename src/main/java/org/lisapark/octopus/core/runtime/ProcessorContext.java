package org.lisapark.octopus.core.runtime;

import org.lisapark.octopus.core.memory.Memory;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public interface ProcessorContext<MEMORY_TYPE> extends SinkContext {

    Memory<MEMORY_TYPE> getProcessorMemory();
}
