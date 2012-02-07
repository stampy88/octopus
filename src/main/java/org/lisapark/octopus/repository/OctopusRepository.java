package org.lisapark.octopus.repository;

import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface OctopusRepository {
    void saveProcessingModel(ProcessingModel model);

    List<ProcessingModel> getProcessingModelsByName(String name);

    List<ExternalSink> getAllExternalSinkTemplates();

    List<ExternalSource> getAllExternalSourceTemplates();

    List<Processor> getAllProcessorTemplates();
}
