package org.lisapark.octopus.core.sink;

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.Persistable;
import org.lisapark.octopus.core.source.Source;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public interface Sink extends Node {

    List<? extends Input> getInputs();

    boolean isConnectedTo(Source source);

    void disconnect(Source source);
}
