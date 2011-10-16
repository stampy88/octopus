package org.matrixlab.octopus.core.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.google.common.collect.Sets;
import org.matrixlab.octopus.core.EventProcessingNode;
import org.matrixlab.octopus.core.EventSink;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;

import java.util.Map;
import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperProcessingNode extends EventProcessingNode<EsperProcessor> {

    private Set<EventSink> sinks = Sets.newHashSet();
    private EPRuntime runtime;

    public EsperProcessingNode(EsperProcessor esperProcessor) {
        super(esperProcessor);
    }

    protected void compile(EPServiceProvider esProvider) {
        /**
         * save a reference to the runtime - we use this for sending event
         * {@link #eventReceived(EventType, Event)}
         */
        this.runtime = esProvider.getEPRuntime();

        EPAdministrator admin = esProvider.getEPAdministrator();
        // register the output event type
        EventType outputEventType = getProcessor().getOutputEventType();
        admin.getConfiguration().addEventType(outputEventType.getName(), outputEventType.getEventDefinition());

        // create the statment and set the subscriber
        EPStatement stmt = admin.createEPL(getProcessor().getStatementForProcessor());
        stmt.setSubscriber(this);
    }

    @Override
    public void eventReceived(EventType eventType, Event event) {
        // we got an event from a source, send it into the esper engine
        runtime.sendEvent(event.getData(), eventType.getName());
    }

    @Override
    public EventType getEventType() {
        // every node is potentially a source of events. The type of this node's events is determined by
        // the processor
        return getProcessor().getOutputEventType();
    }

    @Override
    public void addEventSink(EventSink sink) {
        sinks.add(sink);
    }

    /**
     * Implementation of an Esper subscriber. This method will be called for each row processed by the
     * {@link #processor}
     *
     * @param row data from the select statement of the processor
     * @see com.espertech.esper.client.EPStatement#setSubscriber(Object)
     */
    public void update(Map<String, Object> row) {
        Event newEvent = new Event(row);
        EventType sourceType = getEventType();

        for (EventSink sink : sinks) {
            sink.eventReceived(sourceType, newEvent);
        }
    }

}
