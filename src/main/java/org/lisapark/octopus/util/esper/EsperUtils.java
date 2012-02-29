package org.lisapark.octopus.util.esper;

import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.core.source.Source;

import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class EsperUtils {

    /**
     * Returns the event name that will be used within Esper for the specified source.
     *
     * @param source to get event name for
     * @return event name
     */
    public static String getEventNameForSource(Source source) {
        return getEventNameForUUID(source.getId());
    }

    /**
     * Returns the event name that will be used within Esper for the specified processor.
     *
     * @param processor to get event name for
     * @return event name
     */
    public static String getEventNameForProcessor(CompiledProcessor<?> processor) {
        return getEventNameForUUID(processor.getId());
    }

    static String getEventNameForUUID(UUID id) {
        // esper doesn't like event name that start with a number, so we always add a prefix of '_'
        StringBuilder eventName = new StringBuilder("_");

        String idAsString = id.toString();
        for (int i = 0; i < idAsString.length(); ++i) {
            // esper also doesn't like '-', so we skip them
            if (idAsString.charAt(i) != '-') {
                eventName.append(idAsString.charAt(i));
            }
        }

        return eventName.toString();
    }
}
