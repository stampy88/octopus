package org.lisapark.octopus.core.runtime;

import org.lisapark.octopus.core.memory.Memory;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ProcessorContext<MEMORY_TYPE> extends SinkContext {

    Memory<MEMORY_TYPE> getProcessorMemory();
}
