package org.matrixlab.octopus.core.compiler;

import com.espertech.esper.client.*;
import org.matrixlab.octopus.core.ProcessingModel;
import org.matrixlab.octopus.core.ProcessingRuntime;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.external.ExternalSource;
import org.matrixlab.octopus.core.processor.*;
import org.matrixlab.octopus.core.runtime.EsperRuntime;

import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperCompiler implements Compiler<String, EsperCompiler.EsperContext> {

    public static class OutputDebugger {
        private UUID id;

        public OutputDebugger(UUID id) {
            this.id = id;
        }

        public void update(Map<String, Object> row) {
            System.out.printf("%s - %s\n", id, row);
        }
    }

    String getEventNameForUUID(UUID id) {
        StringBuilder eventName = new StringBuilder("_");

        String idAsString = id.toString();
        for (int i = 0; i < idAsString.length(); ++i) {
            if (idAsString.charAt(i) != '-') {
                eventName.append(idAsString.charAt(i));
            }
        }

        return eventName.toString();
    }

    @Override
    public ProcessingRuntime compile(ProcessingModel model) {
        Configuration configuration = new Configuration();

        // register all of the model source event types
        for (ExternalSource eventSource : model.getExternalSources()) {
            EventType eventType = eventSource.getOutputEventType();

            configuration.addEventType(
                    getEventNameForUUID(eventType.getId()),
                    eventType.getEventDefinition()
            );
        }

        for (Processor processor : model.getProcessors()) {

            if (processor.generatesOutput()) {
                EventType eventType = processor.getOutputEventType();

                configuration.addEventType(
                        getEventNameForUUID(eventType.getId()),
                        eventType.getEventDefinition()
                );
            }
        }

        EPServiceProvider epService = EPServiceProviderManager.getProvider(model.getModelName(), configuration);
        epService.initialize();

        EPAdministrator admin = epService.getEPAdministrator();
        for (Processor processor : model.getProcessors()) {
            String statement = processor.compile(this, new EsperContext(epService));
            EPStatement stmt = admin.createEPL(statement);

            stmt.setSubscriber(new OutputDebugger(processor.getId()));
        }


        return new EsperRuntime(epService, model.getExternalSources());
    }


    @Override
    public String compile(EsperContext esperContext, Addition addition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String compile(EsperContext esperContext, Wma wma) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String compile(EsperContext context, Sma sma) {
        // todo validate the SMA
        Input smaInput = sma.getInput();
        String inputEventName = getEventNameForUUID(smaInput.getSourceId());
        String inputAttributeName = smaInput.getSourceAttribute().getName();

        EventType outputEventType = sma.getOutputEventType();
        String outputEventName = getEventNameForUUID(outputEventType.getId());
        String outputAttributeName = sma.getOutputAttributeName();

        String stmt = String.format("INSERT INTO %s SELECT AVG(%s) as %s FROM %s.win:length(%d)",
                outputEventName,
                inputAttributeName, outputAttributeName,
                inputEventName,
                sma.getWindowLength()
        );

        System.err.println(stmt);

        return stmt;
    }

    public static class EsperContext implements CompilerContext {
        private final EPServiceProvider epService;

        private EsperContext(EPServiceProvider epService) {
            this.epService = epService;
        }
    }
}
