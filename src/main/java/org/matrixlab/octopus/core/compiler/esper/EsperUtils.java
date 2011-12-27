package org.matrixlab.octopus.core.compiler.esper;

import org.matrixlab.octopus.core.event.EventType;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
abstract class EsperUtils {

    static String getEventNameForEventType(EventType eventType) {
        StringBuilder eventName = new StringBuilder("_");

        String idAsString = eventType.getId().toString();
        for (int i = 0; i < idAsString.length(); ++i) {
            if (idAsString.charAt(i) != '-') {
                eventName.append(idAsString.charAt(i));
            }
        }

        return eventName.toString();
    }

}
