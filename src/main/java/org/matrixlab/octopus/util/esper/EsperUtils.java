package org.matrixlab.octopus.util.esper;

import org.matrixlab.octopus.core.processor.CompiledProcessor;
import org.matrixlab.octopus.core.source.Source;

import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class EsperUtils {

    public static String getEventNameForSource(Source source) {

        return getEventNameForUUID(source.getId());
    }

    public static String getEventNameForSource(CompiledProcessor<?> source) {

        return getEventNameForUUID(source.getId());
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
