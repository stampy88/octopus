package org.matrixlab.octopus.core.source.external;

import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.source.Source;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalSource extends Source {

    CompiledExternalSource compile() throws ValidationException;
}

