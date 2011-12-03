package org.matrixlab.octopus.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.matrixlab.octopus.core.external.ExternalSource;
import org.matrixlab.octopus.core.processor.Processor;

import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingModel {

    private final String modelName;

    private final Set<ExternalSource> externalSources = Sets.newHashSet();
    private final Set<Processor> processors = Sets.newHashSet();

    public ProcessingModel(String modelName) {
        this.modelName = modelName;
    }

    public void addExternalEventSource(ExternalSource source) {
        externalSources.add(source);
    }

    public void addProcessor(Processor node) {
        processors.add(node);
    }

    public Set<ExternalSource> getExternalSources() {
        return ImmutableSet.copyOf(externalSources);
    }

    public Set<Processor> getProcessors() {
        return ImmutableSet.copyOf(processors);
    }

    public String getModelName() {
        return modelName;
    }
}
