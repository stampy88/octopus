package org.lisapark.octopus.core.runtime.basic;

import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.runtime.ProcessorContext;

import java.io.PrintStream;

import static com.jgoodies.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public class BasicProcessorContext<MEMORY_TYPE> extends BasicSinkContext implements ProcessorContext<MEMORY_TYPE> {
    private final Memory<MEMORY_TYPE> memory;

    public BasicProcessorContext(PrintStream standardOut) {
        super(standardOut);
        memory = null;
    }

    public BasicProcessorContext(PrintStream standardOut, Memory<MEMORY_TYPE> memory) {
        super(standardOut);
        checkArgument(memory != null, "memory cannot be null");
        this.memory = memory;
    }

    @Override
    public Memory<MEMORY_TYPE> getProcessorMemory() {
        return memory;
    }
}
