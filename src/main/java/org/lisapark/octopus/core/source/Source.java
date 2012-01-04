package org.lisapark.octopus.core.source;

import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.Output;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Source extends Node {

    Output getOutput();
}
