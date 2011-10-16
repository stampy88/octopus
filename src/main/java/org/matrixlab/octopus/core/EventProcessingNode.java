package org.matrixlab.octopus.core;

/**
 * A node is both a source of events and a sink for events.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class EventProcessingNode<P extends Processor> implements EventSink, EventSource {

    private P processor;

    public EventProcessingNode(P processor) {
        this.processor = processor;
    }

    protected P getProcessor() {
        return processor;
    }
}
