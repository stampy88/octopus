package org.matrixlab.octopus.core.event;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Event {
    private final Map<String, Object> data;

    public Event(Map<String, Object> data) {
        this.data = ImmutableMap.copyOf(data);
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                '}';
    }
}
