package org.lisapark.octopus;

import com.google.common.collect.Maps;
import com.jidesoft.plaf.LookAndFeelFactory;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.compiler.esper.EsperCompiler;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.processor.Sma;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.sink.external.ConsoleSink;
import org.lisapark.octopus.core.source.external.TestSource;
import org.lisapark.octopus.service.DefaultExternalSinkService;
import org.lisapark.octopus.service.DefaultExternalSourceService;
import org.lisapark.octopus.service.DefaultProcessorService;
import org.lisapark.octopus.view.ApplicationController;

import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Main {

    public static void main(String[] args) {
        EventType eventType = new EventType();
        eventType.addAttribute(Attribute.stringAttribute("firstName"));
        eventType.addAttribute(Attribute.stringAttribute("lastName"));
        eventType.addAttribute(Attribute.integerAttribute("age"));

        ProcessingModel model = new ProcessingModel("test");

        TestSource testSource = new TestSource(UUID.randomUUID(), "Test Source", "This source produces preconfigured events", eventType);
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
        sma.getInput().connectSource(testSource).setSourceAttribute("age");

        model.addProcessor(sma);

        ConsoleSink consoleSink = ConsoleSink.newTemplate();
        consoleSink.getInput().connectSource(sma);

        model.addExternalSink(consoleSink);

        EsperCompiler compiler = new EsperCompiler();
        ProcessingRuntime runtime = compiler.compile(model);
        runtime.start();

        createView(model);
    }

    private static Event personWithFirstNameLastNameAndAge(String firstName, String lastName, int age) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("age", age);

        return new Event(data);
    }

    public static void createView(ProcessingModel model) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();

        ApplicationController app = new ApplicationController(
                model,
                new DefaultProcessorService(),
                new DefaultExternalSourceService(),
                new DefaultExternalSinkService());
        app.start();

    }
}
