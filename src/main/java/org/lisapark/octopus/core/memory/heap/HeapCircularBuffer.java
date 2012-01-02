package org.lisapark.octopus.core.memory.heap;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.memory.Memory;

import java.util.Collection;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class HeapCircularBuffer<T> implements Memory<T> {

    private final T[] buffer;

    private int currentIndex;

    @SuppressWarnings("unchecked")
    public HeapCircularBuffer(int n) {
        buffer = (T[]) new Object[n];
        currentIndex = 0;
    }

    @Override
    public void add(T value) {
        buffer[currentIndex] = value;

        currentIndex = (currentIndex + 1) % buffer.length;
    }

    @Override
    public boolean remove(T value) {
        throw new UnsupportedOperationException("Remove not supported");
    }

    @Override
    public Collection<T> values() {
        Collection<T> values = Lists.newArrayListWithCapacity(buffer.length);

        for (T item : buffer) {
            if (item != null) {
                values.add(item);
            }
        }

        return values;
    }
}
