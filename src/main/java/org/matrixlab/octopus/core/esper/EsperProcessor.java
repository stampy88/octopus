package org.matrixlab.octopus.core.esper;

import org.matrixlab.octopus.core.Processor;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract interface EsperProcessor extends Processor {

    abstract String getStatementForProcessor();

}
