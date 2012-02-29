package org.lisapark.octopus.util.esper;

import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.core.source.Source;

import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class EsperUtils {

    public static String getEventNameForSource(Source source) {
        return getEventNameForUUID(source.getId());
    }

    public static String getEventNameForSource(CompiledProcessor<?> processor) {
        return getEventNameForUUID(processor.getId());
    }

    static String getEventNameForUUID(UUID id) {
        StringBuilder eventName = new StringBuilder("_");

        String idAsString = id.toString();
        for (int i = 0; i < idAsString.length(); ++i) {
            if (idAsString.charAt(i) != '-') {
                eventName.append(idAsString.charAt(i));
            }
        }

        return eventName.toString();
    }

}
