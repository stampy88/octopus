package org.matrixlab.octopus;

import com.google.common.collect.Maps;
import org.matrixlab.octopus.core.ProcessingModel;
import org.matrixlab.octopus.core.compiler.esper.EsperCompiler;
import org.matrixlab.octopus.core.event.Attribute;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.external.source.TestSource;
import org.matrixlab.octopus.core.processor.Sma;
import org.matrixlab.octopus.core.runtime.ProcessingRuntime;

import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Main {

    public static void main(String[] args) {
        EventType eventType = new EventType(UUID.randomUUID());
        eventType.addAttribute(Attribute.stringAttribute("firstName"));
        eventType.addAttribute(Attribute.stringAttribute("lastName"));
        eventType.addAttribute(Attribute.integerAttribute("age"));

        ProcessingModel model = new ProcessingModel("test");

        TestSource testSource = new TestSource("Test Source", "This source produces preconfigured events", eventType);
        testSource.addEvent(personWithFirstNameLastNameAndAge("John", "Smith", 5));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Dave", "Sinclair", 10));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Yuliya", "Zhurba", 33));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Masha", "Mylnikov", 20));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Inna", "Zhurba", 56));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Andy", "Oswald", 37));
        testSource.addEvent(personWithFirstNameLastNameAndAge("Matt", "Swartley", 42));

        model.addExternalEventSource(testSource);

        Sma sma = Sma.newTemplate();
        sma.setWindowLength(5);
        sma.setOutputAttributeName("averageAge");

        // connect the sma input to the test source's age attribute
        sma.getInput().connectSource(testSource).setSourceAttribute(eventType.getAttributeByName("age"));

        model.addProcessor(sma);

        EsperCompiler compiler = new EsperCompiler();
        ProcessingRuntime runtime = compiler.compile(model);
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
