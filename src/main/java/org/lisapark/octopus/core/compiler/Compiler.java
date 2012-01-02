package org.lisapark.octopus.core.compiler;

import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;

/**
 * A {@link Compiler} is used to take a {@link ProcessingModel} and create a {@link ProcessingRuntime}. There
 * different implementations of the compiler depending on the underlying complex event processing engine.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.lisapark.octopus.core.processor.Processor
 * @see org.lisapark.octopus.core.ProcessingModel
 */
public interface Compiler {

    ProcessingRuntime compile(ProcessingModel model);
}
