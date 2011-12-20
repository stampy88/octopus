package org.matrixlab.octopus.core.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventType {

    private final UUID id;
    private final List<Attribute> attributes = Lists.newLinkedList();

    public EventType(UUID id) {
        this.id = id;
    }

    private EventType(UUID id, EventType copyFromEventType) {
        this.id = id;
        this.attributes.addAll(copyFromEventType.attributes);
    }

    public UUID getId() {
        return id;
    }

    public EventType unionWith(EventType eventType) {
        // todo do we create a new id?
        EventType newEventType = new EventType(this.id);
        newEventType.attributes.addAll(this.attributes);
        newEventType.attributes.addAll(eventType.attributes);

        return newEventType;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public Attribute getAttributeByName(String name) {
        Attribute attr = null;

        for (Attribute candidateAttr : attributes) {
            if (name.equals(candidateAttr.getName())) {
                attr = candidateAttr;
                break;
            }
        }

        return attr;
    }

    public boolean containsAttribute(Attribute attribute) {
        return attributes.contains(attribute);
    }

    public Map<String, Object> getEventDefinition() {
        Map<String, Object> definition = Maps.newHashMap();

        for (Attribute attribute : attributes) {
            definition.put(attribute.getName(), attribute.getType());
        }

        return definition;
    }

    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = Lists.newLinkedList();
        for (Attribute attribute : attributes) {
            attributeNames.add(attribute.getName());
        }
        return attributeNames;
    }

    @Override
    public String toString() {
        return "EventType{" +
                "id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    // TODO is event type reproducible?

    public EventType newInstance() {
        return new EventType(UUID.randomUUID(), this);
    }
}
