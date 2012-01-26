package org.lisapark.octopus.core.event;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lisapark.octopus.core.Copyable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An {@link EventType} is the definition of an {@link Event} that describes some or all of the attributes a event
 * will have.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EventType implements Copyable {

    private final List<Attribute> attributes = Lists.newArrayList();

    public EventType() {
    }

    private EventType(EventType copyFromEventType) {
        for (Attribute copyFromAttribute : copyFromEventType.attributes) {
            this.attributes.add(copyFromAttribute.copyOf());
        }
    }

    public int getNumberOfAttributes() {
        return attributes.size();
    }

    public Attribute getAttributeAt(int index) {
        return attributes.get(index);
    }

    public EventType unionWith(EventType eventType) {
        attributes.addAll(eventType.attributes);

        return this;
    }

    public EventType addAttribute(Attribute attribute) {
        attributes.add(attribute);

        return this;
    }

    public EventType removeAttribute(Attribute attribute) {
        attributes.remove(attribute);

        return this;
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

    public List<Attribute> getAttributes() {
        return ImmutableList.copyOf(attributes);
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
        return Objects.toStringHelper(this).add("attributes", attributes).toString();
    }

    @Override
    public EventType copyOf() {
        return new EventType(this);
    }
}
