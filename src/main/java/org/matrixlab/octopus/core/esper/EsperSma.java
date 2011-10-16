package org.matrixlab.octopus.core.esper;

import com.google.common.base.Joiner;
import org.matrixlab.octopus.core.event.AttributeDefinition;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.processor.Sma;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperSma extends Sma implements EsperProcessor {

    private final EventType outputEventType;

    public EsperSma(EventType sourceEventType, String attributeName, int windowLength) {
        super(sourceEventType, attributeName, windowLength);

        this.outputEventType = sourceEventType.createNewEventTypeWithAdditionalAttribute(
                "avg-" + sourceEventType.getName(),
                AttributeDefinition.doubleAttribute("avg-" + attributeName));
    }

    public String getStatementForProcessor() {
        String eventName = getSourceEventType().getName();

        String commaSeparatedAttributes = Joiner.on(',').join(getSourceEventType().getAttributeNames());

        return String.format("SELECT AVG(%s), %s FROM %s.win:length(%d)",
                getAttributeName(), commaSeparatedAttributes, eventName, getWindowLength());
    }

    @Override
    public EventType getOutputEventType() {
        return outputEventType;
    }
}
