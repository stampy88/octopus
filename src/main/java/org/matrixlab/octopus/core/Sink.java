package org.matrixlab.octopus.core;

import org.matrixlab.octopus.core.processor.Input;

import java.util.Collection;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Sink extends Node {

    Collection<Input> getInputs();
}
