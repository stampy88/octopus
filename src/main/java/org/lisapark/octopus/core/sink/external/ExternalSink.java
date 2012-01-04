package org.lisapark.octopus.core.sink.external;

import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.sink.Sink;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ExternalSink extends Sink {

    CompiledExternalSink compile() throws ValidationException;

    Sink copyOf();
}
