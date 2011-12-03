package org.matrixlab.octopus.core;

import org.matrixlab.octopus.core.event.Attribute;
import org.matrixlab.octopus.core.processor.Input;

import java.util.Collection;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Sink {

    Collection<Input> getInputs();

    void connectInputToSourceAttribute(Input input, Source source, Attribute sourceAttribute) throws ValidationException;
}
