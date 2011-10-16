package org.matrixlab.octopus.core.event;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventType {

    private final String name;
    private final Map<String, AttributeDefinition> attributeDefinitions = Maps.newHashMap();

    public EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public EventType createNewEventTypeWithAdditionalAttribute(String name, AttributeDefinition attributeDefinition) {
        EventType newEventType = new EventType(name);
        newEventType.attributeDefinitions.putAll(this.attributeDefinitions);
        newEventType.attributeDefinitions.put(attributeDefinition.getName(), attributeDefinition);

        return newEventType;
    }

    public AttributeDefinition addAttributeDefinition(AttributeDefinition attributeDefinition) {
        return attributeDefinitions.put(attributeDefinition.getName(), attributeDefinition);
    }

    public AttributeDefinition getAttributeDefinition(String attributeName) {
        return attributeDefinitions.get(attributeName);
    }

    public Map<String, Object> getEventDefinition() {
        Map<String, Object> definition = Maps.newHashMap();

        for (AttributeDefinition attributeDefinition : attributeDefinitions.values()) {
            definition.put(attributeDefinition.getName(), attributeDefinition.getType());
        }

        return definition;
    }

    public Collection<String> getAttributeNames() {
        return Collections.unmodifiableCollection(attributeDefinitions.keySet());
    }

    @Override
    public String toString() {
        return "EventType{" +
                "name='" + name + '\'' +
                ", attributeDefinitions=" + attributeDefinitions +
                '}';
    }
}
