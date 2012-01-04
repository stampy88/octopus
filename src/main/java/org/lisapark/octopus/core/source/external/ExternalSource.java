package org.lisapark.octopus.core.source.external;

import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.source.Source;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalSource extends Source {

    CompiledExternalSource compile() throws ValidationException;

    Source newInstance();

    Source copyOf();
}

