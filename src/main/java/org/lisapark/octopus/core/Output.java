package org.lisapark.octopus.core;

import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.EventType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Output extends AbstractComponent {

    private final EventType eventType;

    protected Output(int id) {
        super(id);
        this.eventType = new EventType();
    }

    protected Output(int id, EventType eventType) {
        super(id);
        this.eventType = eventType;
    }

    protected Output(int id, String name, String description) {
        super(id, name, description);
        this.eventType = new EventType();
    }

    protected Output(Output existingOutput) {
        super(existingOutput);
        this.eventType = existingOutput.eventType.copyOf();
    }

    public EventType getEventType() {
        return eventType.copyOf();
    }

    @Override
    public Output copyOf() {
        return new Output(this);
    }

    public EventType addAttribute(Attribute attribute) {
        return eventType.addAttribute(attribute);
    }

    public boolean containsAttribute(Attribute attribute) {
        return eventType.containsAttribute(attribute);
    }

    public Attribute getAttributeByName(String name) {
        return eventType.getAttributeByName(name);
    }

    public Map<String, Object> getEventDefinition() {
        return eventType.getEventDefinition();
    }

    public List<Attribute> getAttributes() {
        return eventType.getAttributes();
    }

    public Collection<String> getAttributeNames() {
        return eventType.getAttributeNames();
    }

    public static Output outputWithId(int id) {
        return new Output(id);
    }

    public static Output outputWithIdAndEventType(int id, EventType eventType) {
        return new Output(id, eventType);
    }
}
