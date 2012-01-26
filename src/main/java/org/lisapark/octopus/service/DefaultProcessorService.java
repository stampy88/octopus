package org.lisapark.octopus.service;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.processor.Addition;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.Sma;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DefaultProcessorService implements ProcessorService {

    public List<Processor> getAllProcessorTemplates() {
        return Lists.<Processor>newArrayList(
                Sma.newTemplate(),
                Addition.newTemplate()
        );
    }
}
