package org.matrixlab.octopus.core;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ProcessingModel<NODE_TYTPE extends EventProcessingNode> {

    String getName();

    void addExternalEventSource(ExternalEventSource source);

    void addProcessingNode(NODE_TYTPE node);

    ProcessingRuntime createRuntime();
}
