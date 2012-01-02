package org.matrixlab.octopus.core.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Event {
    private final Map<String, Object> data;

    public Event(String attributeName, Object value) {
        this.data = ImmutableMap.of(attributeName, value);
    }

    public Event(Map<String, Object> data) {
        this.data = ImmutableMap.copyOf(data);
    }

    public Event unionWith(Event event) {
        Map<String, Object> newData = Maps.newHashMap(data);
        newData.putAll(event.getData());

        return new Event(newData);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Integer getAttributeAsInteger(String attributeName) {
        return ((Number) data.get(attributeName)).intValue();
    }

    public Short getAttributeAsShort(String attributeName) {
        return ((Number) data.get(attributeName)).shortValue();
    }

    public Long getAttributeAsLong(String attributeName) {
        return ((Number) data.get(attributeName)).longValue();
    }

    public Float getAttributeAsFloat(String attributeName) {
        return ((Number) data.get(attributeName)).floatValue();
    }

    public Double getAttributeAsDouble(String attributeName) {
        // todo return null??
        return ((Number) data.get(attributeName)).doubleValue();
    }

    public String getAttributeAsString(String attributeName) {
        return (String) data.get(attributeName);
    }

    public Boolean getAttributeAsBoolean(String attributeName) {
        return (Boolean) data.get(attributeName);
    }

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                '}';
    }
}
