package org.matrixlab.octopus.core;

import com.google.common.collect.Maps;
import org.matrixlab.octopus.core.esper.EsperProcessingModel;
import org.matrixlab.octopus.core.esper.EsperProcessingNode;
import org.matrixlab.octopus.core.esper.EsperSma;
import org.matrixlab.octopus.core.event.AttributeDefinition;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.sink.ConsoleEventSink;
import org.matrixlab.octopus.core.source.TestEventSource;

import java.util.Map;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Main {

    public static void main(String[] args) {
        EventType eventType = new EventType("person");
        eventType.addAttributeDefinition(AttributeDefinition.stringAttribute("firstName"));
        eventType.addAttributeDefinition(AttributeDefinition.stringAttribute("lastName"));
        eventType.addAttributeDefinition(AttributeDefinition.integerAttribute("age"));

        TestEventSource testSource = new TestEventSource(eventType);
        testSource.addEvent(personWithFirstNameLastNameAndAge("Dave", "Sinclair", 10));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Yuliya", "Zhurba", 30));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Masha", "Mylnikov", 20));

        EsperProcessingNode processingNode = new EsperProcessingNode(new EsperSma(eventType, "age", 10));

        // connect the source to this processing node
        testSource.addEventSink(processingNode);
        // then attach a sink to this processing node
        processingNode.addEventSink(new ConsoleEventSink());

        ProcessingModel<EsperProcessingNode> esperModel = new EsperProcessingModel("TestModel");
        esperModel.addExternalEventSource(testSource);
        esperModel.addProcessingNode(processingNode);

        ProcessingRuntime runtime = esperModel.createRuntime();
        runtime.start();
    }

    private static Event personWithFirstNameLastNameAndAge(String firstName, String lastName, int age) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("age", age);

        return new Event(data);
    }
}
