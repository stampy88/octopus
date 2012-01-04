package org.lisapark.octopus.core.sink.external;

import com.google.common.collect.ImmutableList;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.sink.Sink;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class CompiledExternalSink {
    private final List<? extends Input> inputs;
    private final UUID id;

    protected CompiledExternalSink(Sink sink) {
        this.id = sink.getId();
        this.inputs = sink.getInputs();
    }

    public UUID getId() {
        return id;
    }

    public List<? extends Input> getInputs() {
        return ImmutableList.copyOf(inputs);
    }

    public abstract void processEvent(Map<Integer, Event> eventsByInputId);
}
