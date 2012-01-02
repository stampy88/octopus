package org.lisapark.octopus.core.compiler.esper;

import com.espertech.esper.client.*;
import com.google.common.collect.Lists;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.memory.heap.HeapMemoryProvider;
import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.runtime.esper.EsperRuntime;
import org.lisapark.octopus.core.source.external.CompiledExternalSource;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.util.esper.EsperUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperCompiler implements org.lisapark.octopus.core.compiler.Compiler {

    public static class OutputDebugger {
        private UUID id;

        public OutputDebugger(UUID id) {
            this.id = id;
        }

        public void update(Map<String, Object> row) {
            System.out.printf("%s - %s\n", id, row);
        }
    }

    void registerEventTypesForModel(Configuration configuration, ProcessingModel model) {
        // register all of the model source event types
        for (ExternalSource externalSource : model.getExternalSources()) {
            // todo do we actually move this onto the source and not eventType?
            EventType eventType = externalSource.getOutputEventType();

            configuration.addEventType(
                    EsperUtils.getEventNameForSource(externalSource),
                    eventType.getEventDefinition()
            );
        }

        for (Processor processor : model.getProcessors()) {

            if (processor.generatesOutput()) {
                EventType eventType = processor.getOutputEventType();

                configuration.addEventType(
                        EsperUtils.getEventNameForSource(processor),
                        eventType.getEventDefinition()
                );
            }
        }
    }

    @Override
    public ProcessingRuntime compile(ProcessingModel model) {
        checkArgument(model != null, "model cannot be null");

        Configuration configuration = new Configuration();

        registerEventTypesForModel(configuration, model);


        EPServiceProvider epService = EPServiceProviderManager.getProvider(model.getModelName(), configuration);
        epService.initialize();

        Collection<CompiledExternalSource> compiledSources = compileExternalSources(model.getExternalSources());
        compileProcessors(epService, model.getProcessors());

        return new EsperRuntime(epService, compiledSources);
    }

    private Collection<CompiledProcessor<?>> compileProcessors(EPServiceProvider epService, Collection<Processor> processors) {
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        Collection<CompiledProcessor<?>> compiledProcessors = Lists.newLinkedList();

        for (Processor processor : processors) {
            // todo - check for output?
            Memory<?> processorMemory = processor.createMemoryForProcessor(new HeapMemoryProvider());
            CompiledProcessor<?> compiledProcessor = processor.compile();
            String statement = getStatementForCompiledProcessor(compiledProcessor);

            EPStatement stmt = admin.createEPL(statement);
            EsperProcessorAdaptor runner = new EsperProcessorAdaptor(compiledProcessor, processorMemory, runtime);

            stmt.setSubscriber(runner);

            // todo this is temporary think we want the output to have the id possibly
            String outputEventName = EsperUtils.getEventNameForSource(processor);
            System.out.println("Compiler " + outputEventName);
            String debugStmt = String.format("select * from %s", outputEventName);
            stmt = admin.createEPL(debugStmt);
            stmt.setSubscriber(new OutputDebugger(processor.getId()));

            compiledProcessors.add(compiledProcessor);
        }

        return compiledProcessors;
    }

    private Collection<CompiledExternalSource> compileExternalSources(Set<ExternalSource> externalSources) throws ValidationException {
        Collection<CompiledExternalSource> compiledSources = Lists.newLinkedList();

        for (ExternalSource externalSource : externalSources) {
            compiledSources.add(externalSource.compile());
        }

        return compiledSources;
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

            String inputName = EsperUtils.getEventNameForSource(input.getSource());

            String aliasName = "_" + aliasIndex++;
            selectClause.append(aliasName).append(".*");
            fromClause.append(inputName).append(".win:length(1) as ").append(aliasName);
        }

        return String.format("SELECT %s FROM %s", selectClause, fromClause);
    }
}
