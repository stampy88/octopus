package org.matrixlab.octopus.core.processor;

import org.matrixlab.octopus.core.Processor;
import org.matrixlab.octopus.core.event.AttributeDefinition;
import org.matrixlab.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Sma implements Processor {

    private final EventType sourceEventType;
    private final String attributeName;
    private final int windowLength;

    public Sma(EventType sourceEventType, String attributeName, int windowLength) {
        this.sourceEventType = sourceEventType;
        this.windowLength = windowLength;

        AttributeDefinition definition = sourceEventType.getAttributeDefinition(attributeName);
        if (definition == null) {
            throw new IllegalArgumentException(String.format("%s is not a valid attribute name for event type %s",
                    attributeName, sourceEventType.getName()));
        }
        if(!definition.isNumeric()) {
            throw new IllegalArgumentException(String.format("%s has to be a numeric type in order to be averaged",
                    attributeName));
        }

        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public EventType getSourceEventType() {
        return sourceEventType;
    }

    public int getWindowLength() {
        return windowLength;
    }
}
