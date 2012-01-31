package org.lisapark.octopus.dao;

import org.lisapark.octopus.core.ProcessingModel;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface OctopusDao {
    void storeProcessingModel(ProcessingModel model);

    List<ProcessingModel> getProcessingModelsByName(String name);
}
