package org.lisapark.octopus.core.event;

import com.google.common.collect.Maps;
import org.lisapark.octopus.core.Persistable;

import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public class Event {
    private final Map<String, Object> data = Maps.newHashMap();

    public Event(String attributeName, Object value) {
        data.put(attributeName, value);
    }

    public Event(Map<String, Object> data) {
        this.data.putAll(data);
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
        Object value = data.get(attributeName);

        if (value != null) {
            return ((Number) value).intValue();
        } else {
            return null;
        }
    }

    public Short getAttributeAsShort(String attributeName) {
        Object value = data.get(attributeName);

        if (value != null) {
            return ((Number) value).shortValue();
        } else {
            return null;
        }
    }

    public Long getAttributeAsLong(String attributeName) {
        Object value = data.get(attributeName);

        if (value != null) {
            return ((Number) value).longValue();
        } else {
            return null;
        }
    }

    public Float getAttributeAsFloat(String attributeName) {
        Object value = data.get(attributeName);

        if (value != null) {
            return ((Number) value).floatValue();
        } else {
            return null;
        }
    }

    public Double getAttributeAsDouble(String attributeName) {
        Object value = data.get(attributeName);

        if (value != null) {
            return ((Number) value).doubleValue();
        } else {
            return null;
        }
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
