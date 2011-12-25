package org.matrixlab.octopus.core.memory;

/**
 * A {@link MemoryProvider} is used by {@link org.matrixlab.octopus.core.processor.Processor}s that need temporary
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface MemoryProvider {

    <T> Memory<T> createCircularBuffer(int bufferSize);
}
