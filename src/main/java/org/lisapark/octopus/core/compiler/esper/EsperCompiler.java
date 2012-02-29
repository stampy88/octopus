package org.lisapark.octopus.core.compiler.esper;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.memory.Memory;
import org.lisapark.octopus.core.memory.MemoryProvider;
import org.lisapark.octopus.core.memory.heap.HeapMemoryProvider;
import org.lisapark.octopus.core.processor.CompiledProcessor;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.processor.ProcessorJoin;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.runtime.ProcessorContext;
import org.lisapark.octopus.core.runtime.basic.BasicProcessorContext;
import org.lisapark.octopus.core.runtime.basic.BasicSinkContext;
import org.lisapark.octopus.core.runtime.esper.EsperRuntime;
import org.lisapark.octopus.core.sink.external.CompiledExternalSink;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.CompiledExternalSource;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.util.esper.EsperUtils;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperCompiler extends org.lisapark.octopus.core.compiler.Compiler {

    private MemoryProvider memoryProvider = new HeapMemoryProvider();
    private PrintStream standardOut = System.out;
    private PrintStream standardError = System.err;

    @Override
    public synchronized void setMemoryProvider(MemoryProvider memoryProvider) {
        checkArgument(memoryProvider != null, "memoryProvider cannot be null");
        this.memoryProvider = memoryProvider;
    }

    @Override
    public synchronized void setStandardOut(PrintStream standardOut) {
        checkArgument(standardOut != null, "standardOut cannot be null");
        this.standardOut = standardOut;
    }

    @Override
    public synchronized void setStandardError(PrintStream stadardError) {
        checkArgument(stadardError != null, "standardError cannot be null");
        this.standardError = stadardError;
    }

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
    public synchronized ProcessingRuntime compile(ProcessingModel model) throws ValidationException {
        checkArgument(model != null, "model cannot be null");

        // ensure we have at least one source
        if (model.getExternalSources().size() == 0) {
            throw new ValidationException(
                    String.format("The model '%s' must have at least one source configured.", model.getModelName())
            );
        }

        // create a new Esper Configuration
        Configuration configuration = new Configuration();

        registerEventTypesForModel(configuration, model);

        EPServiceProvider epService = EPServiceProviderManager.getProvider(model.getModelName(), configuration);
        epService.initialize();

        List<String> errors = Lists.newLinkedList();

        Collection<CompiledExternalSource> compiledSources = compileExternalSources(model.getExternalSources(), errors);
        compileProcessors(epService, model.getProcessors(), errors);
        compileSinks(epService, model.getExternalSinks(), errors);

        if (errors.size() > 0) {
            throw new ValidationException(Joiner.on('\n').join(errors));
        }

        return new EsperRuntime(epService, compiledSources, standardOut, standardError);
    }

    private void compileSinks(EPServiceProvider epService, Set<ExternalSink> externalSinks, List<String> errors) {
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        for (ExternalSink externalSink : externalSinks) {
            try {
                CompiledExternalSink compiledExternalSink = externalSink.compile();

                String statement = getStatementForCompiledSink(compiledExternalSink);
                EPStatement stmt = admin.createEPL(statement);

                EsperExternalSinkAdaptor runner = new EsperExternalSinkAdaptor(
                        compiledExternalSink, new BasicSinkContext(standardOut, standardError), runtime
                );
                stmt.setSubscriber(runner);
            } catch (ValidationException e) {
                errors.add(e.getLocalizedMessage());

            } catch (EPException e) {
                errors.add(e.getLocalizedMessage());
            }
        }
    }

    private Collection<CompiledProcessor<?>> compileProcessors(EPServiceProvider epService, Collection<Processor> processors, List<String> errors) {
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        Collection<CompiledProcessor<?>> compiledProcessors = Lists.newLinkedList();

        for (Processor processor : processors) {
            Memory processorMemory = processor.createMemoryForProcessor(memoryProvider);

            try {
                CompiledProcessor<?> compiledProcessor = processor.compile();
                String statement = getStatementForCompiledProcessor(compiledProcessor);

                EPStatement stmt = admin.createEPL(statement);

                ProcessorContext ctx;
                if (processorMemory != null) {
                    ctx = new BasicProcessorContext(standardOut, standardError, processorMemory);
                } else {
                    ctx = new BasicProcessorContext(standardOut, standardError);
                }

                EsperProcessorAdaptor runner = new EsperProcessorAdaptor(compiledProcessor, ctx, runtime);
                stmt.addListener(runner);

                compiledProcessors.add(compiledProcessor);
            } catch (ValidationException e) {
                errors.add(e.getLocalizedMessage());

            } catch (EPException e) {
                errors.add(e.getLocalizedMessage());
            }
        }

        return compiledProcessors;
    }

    private Collection<CompiledExternalSource> compileExternalSources(Set<ExternalSource> externalSources, List<String> errors) {
        Collection<CompiledExternalSource> compiledSources = Lists.newLinkedList();

        for (ExternalSource externalSource : externalSources) {
            try {
                compiledSources.add(externalSource.compile());
            } catch (ValidationException e) {
                errors.add(e.getLocalizedMessage());
            }
        }

        return compiledSources;
    }

    String getStatementForCompiledProcessor(CompiledProcessor<?> compiledProcessor) {
        // get inputs
        StringBuilder selectClause = new StringBuilder();
        StringBuilder fromClause = new StringBuilder();

        List<ProcessorInput> inputs = compiledProcessor.getInputs();

        Map<ProcessorInput, String> inputToAlias = Maps.newHashMap();
        int aliasIndex = 0;
        for (ProcessorInput input : inputs) {
            if (selectClause.length() > 0) {
                selectClause.append(", ");
                fromClause.append(", ");
            }

            String inputName = EsperUtils.getEventNameForSource(input.getSource());

            String aliasName = "_" + aliasIndex++;
            inputToAlias.put(input, aliasName);

            selectClause.append(aliasName).append(".* as ").append(aliasName).append("_properties");
            fromClause.append(inputName).append(".win:length(1) as ").append(aliasName);
        }

        StringBuilder whereClause = new StringBuilder();
        List<ProcessorJoin> joins = compiledProcessor.getJoins();
        for (ProcessorJoin join : joins) {
            // some joins aren't required as they are on the same input
            if (join.isRequired()) {
                String firstAlias = inputToAlias.get(join.getFirstInput());
                String secondAlias = inputToAlias.get(join.getSecondInput());

                if (whereClause.length() > 0) {
                    whereClause.append("AND ");
                }

                whereClause.append(firstAlias).append('.').append(join.getFirstInputAttributeName());
                whereClause.append(" = ");
                whereClause.append(secondAlias).append('.').append(join.getSecondInputAttributeName());
            }
        }

        if (whereClause.length() == 0) {
            return String.format("SELECT %s FROM %s", selectClause, fromClause);
        } else {
            return String.format("SELECT %s FROM %s WHERE %s", selectClause, fromClause, whereClause);
        }
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
