package org.lisapark.octopus.core.compiler.esper;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;
import com.google.common.collect.Maps;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.processor.ProcessorJoin;
import org.lisapark.octopus.core.runtime.ProcessorContext;
import org.lisapark.octopus.util.Pair;
import org.lisapark.octopus.util.esper.EsperUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class EsperProcessorAdaptor implements UpdateListener {
    private final CompiledProcessor processor;
    private final Pair<String, ProcessorInput>[] sourceIdToInput;
    private final String outputAttributeName;
    private final String outputEventId;

    private final ProcessorContext ctx;
    private final EPRuntime runtime;

    @SuppressWarnings("unchecked")
    EsperProcessorAdaptor(CompiledProcessor<?> processor, ProcessorContext<?> ctx, EPRuntime runtime) {
        this.processor = processor;
        this.ctx = ctx;
        this.runtime = runtime;

        this.sourceIdToInput = (Pair<String, ProcessorInput>[]) new Pair[processor.getInputs().size()];

        int index = 0;
        for (ProcessorInput input : processor.getInputs()) {
            String sourceId = EsperUtils.getEventNameForSource(input.getSource());
            sourceIdToInput[index++] = Pair.newInstance(sourceId, input);
        }

        outputAttributeName = processor.getOutput().getAttributeName();
        outputEventId = EsperUtils.getEventNameForSource(processor);
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (isMapEvent(newEvents)) {
            MapEventBean mapEvent = (MapEventBean) newEvents[0];

            Map<Integer, Event> eventsByInputId = eventsByInputIdsFromMapEvent(mapEvent);

            @SuppressWarnings("unchecked")
            Object output = processor.processEvent(ctx, eventsByInputId);

            if (output != null && outputAttributeName != null) {
                // todo create new event based on old event - what about name collisions??

                Event outputEvent = new Event(outputAttributeName, output);
                outputEvent = outputEvent.unionWith(eventsByInputId.values());

                runtime.sendEvent(outputEvent.getData(), outputEventId);
            }
        }
    }

    /**
     * Returns true if the specified set of {@link EventBean}s is non-null and the first item of which is a
     * {@link MapEventBean}
     *
     * @param newEvents to inspect
     * @return true if the newEvents is non-null and first element is a MapEventBean
     */
    private boolean isMapEvent(EventBean[] newEvents) {
        return newEvents != null && newEvents.length > 0 && newEvents[0] instanceof MapEventBean;
    }

    private Map<Integer, Event> eventsByInputIdsFromMapEvent(MapEventBean mapEvent) {
        Collection<Object> mapEventBeans = mapEvent.getProperties().values();
        Map<Integer, Event> eventsByInputId = Maps.newHashMapWithExpectedSize(mapEventBeans.size());

        for (Object mapEventBeanObj : mapEventBeans) {
            MapEventBean mapEventBean = (MapEventBean) mapEventBeanObj;

            ProcessorInput input = getInputForSourceId(mapEventBean.getEventType().getName());

            if (input != null) {
                // put the event for the input
                eventsByInputId.put(input.getId(), new Event(mapEventBean.getProperties()));

                // if the input is part of a join, BUT the join is not required we need to put the SAME event in for the
                // other side of the join
                ProcessorJoin join = processor.getJoinForInput(input);
                if (join != null && !join.isRequired()) {
                    ProcessorInput otherInput = join.getOtherInput(input);

                    eventsByInputId.put(otherInput.getId(), new Event(mapEventBean.getProperties()));
                }
            }
        }

        return eventsByInputId;
    }

    private ProcessorInput getInputForSourceId(String sourceId) {
        ProcessorInput input = null;

        for (int index = 0; index < sourceIdToInput.length; ++index) {
            if (sourceIdToInput[index].getFirst().equals(sourceId)) {
                input = sourceIdToInput[index].getSecond();
                break;
            }
        }

        return input;
    }
}
