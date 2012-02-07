package org.lisapark.octopus.core.source;

import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.Persistable;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public interface Source extends Node {

    Output getOutput();
}
