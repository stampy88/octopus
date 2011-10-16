package org.matrixlab.octopus.core.event;

import java.util.Date;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class AttributeDefinition<T> {

    private final String name;
    private final Class<T> type;

    private AttributeDefinition(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isNumeric() {
        return Number.class.isAssignableFrom(this.type);
    }

    @Override
    public String toString() {
        return "AttributeDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public static AttributeDefinition<String> stringAttribute(String name) {
        return new AttributeDefinition<String>(name, String.class);
    }

    public static AttributeDefinition<Integer> integerAttribute(String name) {
        return new AttributeDefinition<Integer>(name, Integer.class);
    }

    public static AttributeDefinition<Long> longAttribute(String name) {
        return new AttributeDefinition<Long>(name, Long.class);
    }

    public static AttributeDefinition<Float> floatAttribute(String name) {
        return new AttributeDefinition<Float>(name, Float.class);
    }

    public static AttributeDefinition<Double> doubleAttribute(String name) {
        return new AttributeDefinition<Double>(name, Double.class);
    }

    public static AttributeDefinition<Date> dateAttribute(String name) {
        return new AttributeDefinition<Date>(name, Date.class);
    }
}
