package org.matrixlab.octopus.core.compiler;

import org.matrixlab.octopus.core.ProcessingModel;
import org.matrixlab.octopus.core.runtime.ProcessingRuntime;

/**
 * A {@link Compiler} is used to take a {@link ProcessingModel} and create a {@link ProcessingRuntime}. There
 * different implementations of the compiler depending on the underlying complex event processing engine.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.matrixlab.octopus.core.processor.Processor
 * @see org.matrixlab.octopus.core.ProcessingModel
 */
public interface Compiler {

    ProcessingRuntime compile(ProcessingModel model);
}
