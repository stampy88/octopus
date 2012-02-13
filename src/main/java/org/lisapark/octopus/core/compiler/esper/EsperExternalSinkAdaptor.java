package org.lisapark.octopus.core.compiler.esper;

import com.espertech.esper.client.EPRuntime;
import com.google.common.collect.Maps;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.sink.external.CompiledExternalSink;
import org.lisapark.octopus.util.Pair;
import org.lisapark.octopus.util.esper.EsperUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class EsperExternalSinkAdaptor {
    private final CompiledExternalSink externalSink;
    private final Pair<String, Integer>[] sourceIdToInputId;

    private final EPRuntime runtime;

    @SuppressWarnings("unchecked")
    EsperExternalSinkAdaptor(CompiledExternalSink externalSink, EPRuntime runtime) {
        this.externalSink = externalSink;
        this.runtime = runtime;

        this.sourceIdToInputId = (Pair<String, Integer>[]) new Pair[externalSink.getInputs().size()];

        int index = 0;
        for (Input input : externalSink.getInputs()) {
            String sourceId = EsperUtils.getEventNameForSource(input.getSource());
            Integer inputId = input.getId();
            sourceIdToInputId[index++] = Pair.newInstance(sourceId, inputId);
        }
    }

    Pair<String, Integer>[] getSourceIdToInputId() {
        return Arrays.copyOf(sourceIdToInputId, sourceIdToInputId.length);
    }

    public void update(Map<String, Object> eventFromInput_1) {
        Event event = new Event(eventFromInput_1);
        Map<Integer, Event> eventsByInputId = Maps.newHashMapWithExpectedSize(1);
        eventsByInputId.put(sourceIdToInputId[0].getSecond(), event);

        externalSink.processEvent(eventsByInputId);
    }

    public void update(Event eventFromInput_1, Event eventFromInput_2) {
        // todo multiple inputs for sinks?
    }
}
