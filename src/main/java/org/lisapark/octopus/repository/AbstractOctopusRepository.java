package org.lisapark.octopus.repository;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.processor.Addition;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.Sma;
import org.lisapark.octopus.core.sink.external.ConsoleSink;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.core.source.external.SqlQuerySource;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class AbstractOctopusRepository implements OctopusRepository {
    @Override
    public List<ExternalSink> getAllExternalSinkTemplates() {
        return Lists.<ExternalSink>newArrayList(ConsoleSink.newTemplate());
    }

    @Override
    public List<ExternalSource> getAllExternalSourceTemplates() {
        return Lists.<ExternalSource>newArrayList(SqlQuerySource.newTemplate());
    }

    @Override
    public List<Processor> getAllProcessorTemplates() {
        return Lists.<Processor>newArrayList(
                Sma.newTemplate(),
                Addition.newTemplate()
        );
    }
}
