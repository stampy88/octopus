package org.lisapark.octopus.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public class ProcessingModel implements Validatable {

    private String modelName;
    private DateTime lastSaved;

    private final Set<ExternalSource> externalSources = Sets.newHashSet();
    private final Set<Processor> processors = Sets.newHashSet();
    private final Set<ExternalSink> externalSinks = Sets.newHashSet();

    public ProcessingModel(String modelName) {
        this.modelName = modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public DateTime getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(DateTime lastSaved) {
        this.lastSaved = lastSaved;
    }

    public void addExternalEventSource(ExternalSource source) {
        externalSources.add(source);
    }

    /**
     * Removes the specified {@link org.lisapark.octopus.core.source.external.ExternalSource} from this model.
     * Doing so will remove any connections between this source and any other sink or processor.
     *
     * @param source to remove from model
     */
    public void removeExternalEventSource(ExternalSource source) {
        checkArgument(externalSources.contains(source), "Model does not contain source " + source);

        for (ExternalSink candidateSink : externalSinks) {
            if (candidateSink.isConnectedTo(source)) {

                candidateSink.disconnect(source);
            }
        }

        for (Processor candidateProcessor : processors) {
            if (candidateProcessor.isConnectedTo(source)) {

                candidateProcessor.disconnect(source);
            }
        }

        externalSources.remove(source);
    }

    public void addExternalSink(ExternalSink sink) {
        externalSinks.add(sink);
    }

    /**
     * Removes the specified {@link org.lisapark.octopus.core.sink.external.ExternalSink} from this model.
     *
     * @param sink to remove from model
     */
    public void removeExternalEventSink(ExternalSink sink) {
        checkArgument(externalSinks.contains(sink), "Model does not contain sink " + sink);

        externalSinks.remove(sink);
    }

    public void addProcessor(Processor processor) {
        processors.add(processor);
    }

    /**
     * Removes the specified {@link org.lisapark.octopus.core.processor.Processor} from this model.
     * Doing so will remove any connections between this processor and any other sink or processor.
     *
     * @param processor to remove from model
     */
    public void removeProcessor(Processor processor) {
        checkArgument(processors.contains(processor), "Model does not contain sink " + processor);

        for (ExternalSink candidateSink : externalSinks) {
            if (candidateSink.isConnectedTo(processor)) {

                candidateSink.disconnect(processor);
            }
        }

        for (Processor candidateProcessor : processors) {
            if (candidateProcessor.isConnectedTo(processor)) {

                candidateProcessor.disconnect(processor);
            }
        }

        processors.remove(processor);
    }

    public Set<ExternalSource> getExternalSources() {
        return ImmutableSet.copyOf(externalSources);
    }

    public Set<ExternalSink> getExternalSinks() {
        return ImmutableSet.copyOf(externalSinks);
    }

    public Set<Processor> getProcessors() {
        return ImmutableSet.copyOf(processors);
    }

    public String getModelName() {
        return modelName;
    }

    /**
     * Validates the {@link #externalSources}, {@link #processors} and {@link #externalSinks} for this model.
     *
     * @throws ValidationException thrown if any source, processor, or sink is invalid.
     */
    @Override
    public void validate() throws ValidationException {
        // todo verify all connections??
        for (ExternalSource source : externalSources) {
            source.validate();
        }

        for (Processor<?> processor : processors) {
            processor.validate();
        }

        for (ExternalSink sink : externalSinks) {
            sink.validate();
        }
    }
}
