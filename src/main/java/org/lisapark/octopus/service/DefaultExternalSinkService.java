package org.lisapark.octopus.service;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.sink.external.ConsoleSink;
import org.lisapark.octopus.core.sink.external.ExternalSink;

import java.util.List;

/**
 * dave sinclair(david.sinclair@lisa-park.com)
 */
public class DefaultExternalSinkService implements ExternalSinkService {

    @Override
    public List<ExternalSink> getAllExternalSinkTemplates() {
        return Lists.<ExternalSink>newArrayList(ConsoleSink.newTemplate());
    }
}
