package org.matrixlab.octopus.core.compiler;

/**
 * Different {@link org.matrixlab.octopus.core.compiler.Compiler} implementations can use a {@link CompilerContext} for storing compiler specific information
 * that can be passed between the {@link org.matrixlab.octopus.core.compiler.Compiler} and a {@link org.matrixlab.octopus.core.processor.Processor}.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.matrixlab.octopus.core.processor.Processor#compile(org.matrixlab.octopus.core.compiler.Compiler, CompilerContext)
 */
public interface CompilerContext {
}
