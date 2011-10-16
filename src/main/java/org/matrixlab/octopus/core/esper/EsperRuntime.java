package org.matrixlab.octopus.core.esper;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import org.matrixlab.octopus.core.ExternalEventSource;
import org.matrixlab.octopus.core.ProcessingRuntime;
import org.matrixlab.octopus.core.event.Event;

import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperRuntime implements ProcessingRuntime {

    private final EPServiceProvider epService;
    private final Set<ExternalEventSource> externalSources;

    public EsperRuntime(EPServiceProvider epService, Set<ExternalEventSource> externalSources) {
        this.epService = epService;
        this.externalSources = externalSources;
    }

    @Override
    public void start() {
        epService.getEPAdministrator().startAllStatements();

        EPRuntime runtime = epService.getEPRuntime();

        for (ExternalEventSource source : externalSources) {
            Event event = source.readEvent();
            while (event != null) {
                runtime.sendEvent(event.getData(), source.getEventType().getName());

                event = source.readEvent();
            }
        }
    }
}
