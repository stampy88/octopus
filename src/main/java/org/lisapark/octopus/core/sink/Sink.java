package org.lisapark.octopus.core.sink;

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Node;

import java.util.Collection;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Sink extends Node {

    Collection<Input> getInputs();
}
