package org.lisapark.octopus.service;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.core.source.external.SqlQuerySource;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DefaultExternalSourceService implements ExternalSourceService {

    @Override
    public List<ExternalSource> getAllExternalSourceTemplates() {
        return Lists.<ExternalSource>newArrayList(SqlQuerySource.newTemplate());
    }
}
