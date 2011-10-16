package org.matrixlab.octopus.core.esper;

import com.espertech.esper.client.*;
import com.google.common.collect.Sets;
import org.matrixlab.octopus.core.ExternalEventSource;
import org.matrixlab.octopus.core.ProcessingModel;
import org.matrixlab.octopus.core.ProcessingRuntime;
import org.matrixlab.octopus.core.event.EventType;

import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperProcessingModel implements ProcessingModel<EsperProcessingNode> {

    private final Set<ExternalEventSource> externalSources = Sets.newHashSet();
    private final Set<EsperProcessingNode> nodes = Sets.newHashSet();
    private final String modelName;

    public EsperProcessingModel(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String getName() {
        return modelName;
    }

    public void addExternalEventSource(ExternalEventSource source) {
        externalSources.add(source);
    }

    public void addProcessingNode(EsperProcessingNode node) {
        nodes.add(node);
    }

    public ProcessingRuntime createRuntime() {
        Configuration configuration = new Configuration();

        // register all of the model source event types
        for (ExternalEventSource eventSource : externalSources) {
            EventType eventType = eventSource.getEventType();
            configuration.addEventType(eventType.getName(), eventType.getEventDefinition());
        }

        EPServiceProvider epService = EPServiceProviderManager.getProvider(modelName, configuration);
        epService.initialize();

        // compile all of the processing nodes
        for (EsperProcessingNode node : nodes) {
            node.compile(epService);
        }

        return new EsperRuntime(epService, externalSources);
    }
}
