package org.lisapark.octopus.service;

import org.lisapark.octopus.core.processor.Processor;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ProcessorService {

    List<Processor> getAllProcessorTemplates();
}
