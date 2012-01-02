package org.lisapark.octopus.core.compiler.esper;

import com.espertech.esper.client.EPRuntime;
import com.google.common.collect.Maps;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.util.Pair;
import org.lisapark.octopus.util.esper.EsperUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class EsperProcessorAdaptor {
    private final CompiledProcessor processor;
    private final Pair<String, Integer>[] sourceIdToInputId;
    private final String outputAttributeName;
    private final String outputEventId;

    private final Memory memory;
    private final EPRuntime runtime;

    @SuppressWarnings("unchecked")
    EsperProcessorAdaptor(CompiledProcessor<?> processor, Memory memory, EPRuntime runtime) {
        this.processor = processor;
        this.memory = memory;
        this.runtime = runtime;

        this.sourceIdToInputId = (Pair<String, Integer>[]) new Pair[processor.getInputs().size()];

        int index = 0;
        for (Input input : processor.getInputs()) {
            String sourceId = EsperUtils.getEventNameForSource(input.getSource());
            Integer inputId = input.getId();
            sourceIdToInputId[index++] = Pair.newInstance(sourceId, inputId);
        }

        if (processor.generatesOutput()) {
            outputAttributeName = processor.getOutput().getAttributeName();
            outputEventId = EsperUtils.getEventNameForSource(processor);

        } else {
            outputAttributeName = null;
            outputEventId = null;
        }
    }

    Pair<String, Integer>[] getSourceIdToInputId() {
        return Arrays.copyOf(sourceIdToInputId, sourceIdToInputId.length);
    }

    String getOutputAttributeName() {
        return outputAttributeName;
    }

    String getOutputEventId() {
        return outputEventId;
    }

    public void update(Map<String, Object> eventFromInput_1) {
        Event event = new Event(eventFromInput_1);
        Map<Integer, Event> eventsByInputId = Maps.newHashMapWithExpectedSize(1);
        eventsByInputId.put(sourceIdToInputId[0].getSecond(), event);

        @SuppressWarnings("unchecked")
        Object output = processor.processEvent(memory, eventsByInputId);

        if (output != null && outputAttributeName != null) {
            // todo create new event based on old event - what about name collisions??

            Event outputEvent = new Event(outputAttributeName, output);
            outputEvent = outputEvent.unionWith(event);

            runtime.sendEvent(outputEvent.getData(), outputEventId);
        }
    }

    public void update(Event eventFromInput_1, Event eventFromInput_2) {

    }
}
