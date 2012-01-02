package org.lisapark.octopus.core.memory.heap;

import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.memory.MemoryProvider;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class HeapMemoryProvider implements MemoryProvider {
    @Override
    public <T> Memory<T> createCircularBuffer(int bufferSize) {
        checkArgument(bufferSize > 0, "bufferSize has to be greater than zero");

        return new HeapCircularBuffer<T>(bufferSize);
    }
}
