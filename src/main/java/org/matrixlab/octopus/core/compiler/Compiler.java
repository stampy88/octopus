package org.matrixlab.octopus.core.compiler;

import org.matrixlab.octopus.core.ProcessingModel;
import org.matrixlab.octopus.core.ProcessingRuntime;
import org.matrixlab.octopus.core.processor.Addition;
import org.matrixlab.octopus.core.processor.Sma;
import org.matrixlab.octopus.core.processor.Wma;

/**
 * A {@link Compiler} is used to take a {@link ProcessingModel} and create a {@link ProcessingRuntime}. There
 * different implementations of the compiler depending on the underlying complex event processing engine. Each
 * {@link org.matrixlab.octopus.core.processor.Processor} has a compile method on this interface and a corresponding
 * {@link org.matrixlab.octopus.core.processor.Processor#compile(Compiler, Context)} method. This is needed because Java doesn't support dynamic
 * dispatching of methods by runtime parameter types.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @param <T> the type returned from compile an individual {@link org.matrixlab.octopus.core.processor.Processor}
 * @param <CONTEXT> this is used to pass information from the compiler {@link org.matrixlab.octopus.core.processor.Processor} and back.
 * @see org.matrixlab.octopus.core.processor.Processor
 * @see org.matrixlab.octopus.core.ProcessingModel
 */
public interface Compiler<T, CONTEXT extends Compiler.Context> {

    ProcessingRuntime compile(ProcessingModel model);

    T compile(CONTEXT context, Sma sma);

    T compile(CONTEXT context, Addition addition);

    T compile(CONTEXT context, Wma wma);

    /**
     * Different {@link Compiler} implementations can use a {@link Context} for storing compiler specific information
     * that can be passed between the {@link Compiler} and a {@link org.matrixlab.octopus.core.processor.Processor}.
     *
     * @author dave sinclair(david.sinclair@lisa-park.com)
     * @see org.matrixlab.octopus.core.processor.Processor#compile(Compiler, Context)
     */
    public static interface Context {
    }
}
