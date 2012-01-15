package org.lisapark.octopus.service;

import org.lisapark.octopus.core.source.external.ExternalSource;

import java.util.List;

/**
 * dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalSourceService {

    List<ExternalSource> getAllExternalSourceTemplates();
}
