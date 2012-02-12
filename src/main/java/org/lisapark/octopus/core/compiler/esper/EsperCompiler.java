package org.lisapark.octopus.core.compiler.esper;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.google.common.collect.Lists;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.memory.heap.HeapMemoryProvider;
import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.runtime.esper.EsperRuntime;
import org.lisapark.octopus.core.sink.external.CompiledExternalSink;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.CompiledExternalSource;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.util.esper.EsperUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperCompiler implements org.lisapark.octopus.core.compiler.Compiler {

    void registerEventTypesForModel(Configuration configuration, ProcessingModel model) {
        // register all of the model source event types
        for (ExternalSource externalSource : model.getExternalSources()) {
            Map<String, Object> eventDefinition = externalSource.getOutput().getEventDefinition();

            configuration.addEventType(
                    EsperUtils.getEventNameForSource(externalSource),
                    eventDefinition
            );
        }

        for (Processor processor : model.getProcessors()) {

            Map<String, Object> eventDefinition = processor.getOutput().getEventDefinition();

            configuration.addEventType(
                    EsperUtils.getEventNameForSource(processor),
                    eventDefinition
            );
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
        compileSinks(epService, model.getExternalSinks());

        return new EsperRuntime(epService, compiledSources);
    }

    private void compileSinks(EPServiceProvider epService, Set<ExternalSink> externalSinks) {
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        for (ExternalSink externalSink : externalSinks) {
            CompiledExternalSink compiledExternalSink = externalSink.compile();

            String statement = getStatementForCompiledSink(compiledExternalSink);
            EPStatement stmt = admin.createEPL(statement);

            EsperExternalSinkAdaptor runner = new EsperExternalSinkAdaptor(compiledExternalSink, runtime);
            stmt.setSubscriber(runner);
        }
    }

    private Collection<CompiledProcessor<?>> compileProcessors(EPServiceProvider epService, Collection<Processor> processors) {
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        Collection<CompiledProcessor<?>> compiledProcessors = Lists.newLinkedList();

        for (Processor processor : processors) {
            Memory<?> processorMemory = processor.createMemoryForProcessor(new HeapMemoryProvider());
            CompiledProcessor<?> compiledProcessor = processor.compile();
            String statement = getStatementForCompiledProcessor(compiledProcessor);

            EPStatement stmt = admin.createEPL(statement);
            EsperProcessorAdaptor runner = new EsperProcessorAdaptor(compiledProcessor, processorMemory, runtime);

            stmt.setSubscriber(runner);

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

        List<ProcessorInput> inputs = compiledProcessor.getInputs();

        // todo join inputs

        int aliasIndex = 0;
        for (ProcessorInput input : inputs) {
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

    String getStatementForCompiledSink(CompiledExternalSink compiledExternalSink) {
        // get inputs
        StringBuilder selectClause = new StringBuilder();
        StringBuilder fromClause = new StringBuilder();

        List<? extends Input> inputs = compiledExternalSink.getInputs();

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
