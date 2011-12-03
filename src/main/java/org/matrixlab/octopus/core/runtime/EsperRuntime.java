package org.matrixlab.octopus.core.runtime;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import org.matrixlab.octopus.core.ProcessingRuntime;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.external.ExternalSource;

import java.util.Set;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperRuntime implements ProcessingRuntime {
    private final EPServiceProvider epService;
    private final Set<ExternalSource> externalSources;

    public EsperRuntime(EPServiceProvider epService, Set<ExternalSource> externalSources) {
        this.epService = epService;
        this.externalSources = externalSources;
    }

    @Override
    public void start() {
        epService.getEPAdministrator().startAllStatements();

        EPRuntime runtime = epService.getEPRuntime();

        for (ExternalSource source : externalSources) {
            Event event = source.readEvent();
            while (event != null) {
                runtime.sendEvent(event.getData(), getEventNameForUUID(source.getId()));

                event = source.readEvent();
            }
        }
    }

    String getEventNameForUUID(UUID id) {
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
