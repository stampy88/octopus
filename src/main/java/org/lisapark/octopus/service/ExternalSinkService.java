package org.lisapark.octopus.service;

import org.lisapark.octopus.core.sink.external.ExternalSink;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalSinkService {

    List<ExternalSink> getAllExternalSinkTemplates();
}
