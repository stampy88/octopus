package org.matrixlab.octopus.core.sink;

import org.matrixlab.octopus.core.Input;
import org.matrixlab.octopus.core.Node;

import java.util.Collection;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Sink extends Node {

    Collection<Input> getInputs();
}
