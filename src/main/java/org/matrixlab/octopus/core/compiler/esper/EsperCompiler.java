package org.matrixlab.octopus.core.compiler.esper;

import com.espertech.esper.client.*;
import org.matrixlab.octopus.core.ProcessingModel;
import org.matrixlab.octopus.core.ProcessingRuntime;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.external.ExternalSource;
import org.matrixlab.octopus.core.memory.Memory;
import org.matrixlab.octopus.core.memory.heap.HeapMemoryProvider;
import org.matrixlab.octopus.core.processor.CompiledProcessor;
import org.matrixlab.octopus.core.processor.Input;
import org.matrixlab.octopus.core.processor.Processor;
import org.matrixlab.octopus.core.runtime.EsperRuntime;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperCompiler implements org.matrixlab.octopus.core.compiler.Compiler {

    public static class OutputDebugger {
        private UUID id;

        public OutputDebugger(UUID id) {
            this.id = id;
        }

        public void update(Map<String, Object> row) {
            System.out.printf("%s - %s\n", id, row);
        }
    }

    @Override
    public ProcessingRuntime compile(ProcessingModel model) {
        Configuration configuration = new Configuration();

        // register all of the model source event types
        for (ExternalSource eventSource : model.getExternalSources()) {
            EventType eventType = eventSource.getOutputEventType();

            configuration.addEventType(
                    EsperUtils.getEventNameForUUID(eventType.getId()),
                    eventType.getEventDefinition()
            );
        }

        for (Processor processor : model.getProcessors()) {

            if (processor.generatesOutput()) {
                EventType eventType = processor.getOutputEventType();

                configuration.addEventType(
                        EsperUtils.getEventNameForUUID(eventType.getId()),
                        eventType.getEventDefinition()
                );
            }
        }

        EPServiceProvider epService = EPServiceProviderManager.getProvider(model.getModelName(), configuration);
        epService.initialize();

        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();
        for (Processor processor : model.getProcessors()) {

            // todo - check for output?
            Memory<?> processorMemory = processor.createMemoryForProcessor(new HeapMemoryProvider());
            CompiledProcessor<?> compiledProcessor = processor.compile();
            String statement = getStatementForCompiledProcessor(compiledProcessor);

            EPStatement stmt = admin.createEPL(statement);
            EsperProcessorAdaptor runner = new EsperProcessorAdaptor(compiledProcessor, processorMemory, runtime);

            stmt.setSubscriber(runner);

            // todo this is temporary think we want the output to have the id possibly
            EventType outputEventType = processor.getOutputEventType();
            String outputEventName = EsperUtils.getEventNameForUUID(outputEventType.getId());
            System.out.println("Compiler " + outputEventName);
            String debugStmt = String.format("select * from %s", outputEventName);
            stmt = admin.createEPL(debugStmt);
            stmt.setSubscriber(new OutputDebugger(processor.getId()));
        }


        return new EsperRuntime(epService, model.getExternalSources());
    }

    String getStatementForCompiledProcessor(CompiledProcessor<?> compiledProcessor) {
        // get inputs
        StringBuilder selectClause = new StringBuilder();
        StringBuilder fromClause = new StringBuilder();

        List<Input> inputs = compiledProcessor.getInputs();

        // todo join inputs

        int aliasIndex = 0;
        for (Input input : inputs) {
            if (selectClause.length() > 0) {
                selectClause.append(", ");
                fromClause.append(", ");
            }

            String inputName = EsperUtils.getEventNameForUUID(input.getSource().getId());
            String aliasName = "_" + aliasIndex++;
            selectClause.append(aliasName).append(".*");
            fromClause.append(inputName).append(".win:length(1) as ").append(aliasName);
        }

        return String.format("SELECT %s FROM %s", selectClause, fromClause);
    }
}
