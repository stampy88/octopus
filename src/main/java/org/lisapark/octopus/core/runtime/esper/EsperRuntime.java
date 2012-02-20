package org.lisapark.octopus.core.runtime.esper;

import com.espertech.esper.client.EPServiceProvider;
import org.lisapark.octopus.core.ProcessingException;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.source.Source;
import org.lisapark.octopus.core.source.external.CompiledExternalSource;
import org.lisapark.octopus.util.esper.EsperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EsperRuntime implements ProcessingRuntime {
    private final EPServiceProvider epService;
    private final Collection<CompiledExternalSource> externalSources;

    /**
     * This service is used to run external sources in a background thread.
     */
    private final ExecutorService executorService;
    private final PrintStream standardOut;
    private final PrintStream standardError;

    static enum State {
        NOT_STARTED, RUNNING, SHUTDOWN
    }

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private State currentState = State.NOT_STARTED;

    public EsperRuntime(EPServiceProvider epService, Collection<CompiledExternalSource> externalSources,
                        PrintStream standardOut, PrintStream standardError) {
        checkArgument(epService != null, "epService cannot be null");
        checkArgument(externalSources != null, "externalSources cannot be null");
        checkArgument(standardOut != null, "standardOut cannot be null");
        checkArgument(standardError != null, "standardError cannot be null");
        this.epService = epService;
        this.externalSources = externalSources;
        this.standardOut = standardOut;
        this.standardError = standardError;
        this.executorService = Executors.newFixedThreadPool(externalSources.size());
    }

    protected State getCurrentState() {
        readLock.lock();

        try {
            return currentState;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        boolean interrupted = false;
        boolean shutdownComplete = false;

        readLock.lock();
        try {
            checkState(currentState == State.RUNNING, "Cannot shutdown if the runtime is not running");

            while (!shutdownComplete) {
                executorService.shutdown();

                try {
                    shutdownComplete = executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            readLock.unlock();
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() throws IllegalStateException {
        writeLock.lock();

        try {
            if (currentState != State.NOT_STARTED) {
                throw new IllegalStateException(String.format("Cannot start runtime unless status is %s", State.NOT_STARTED));
            }

            currentState = State.RUNNING;

            // start all the statements
            epService.getEPAdministrator().startAllStatements();

            for (CompiledExternalSource source : externalSources) {
                executorService.submit(new ExternalSourceDrainer(source, this, standardError));
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void sendEventFromSource(Event event, Source source) {
        readLock.lock();

        try {
            checkState(currentState == State.RUNNING, "Cannot send an event unless the runtime has been started");

            epService.getEPRuntime().sendEvent(event.getData(), EsperUtils.getEventNameForSource(source));
        } finally {
            readLock.unlock();
        }
    }

    /**
     * @author dave sinclair(david.sinclair@lisa-park.com)
     */
    static class ExternalSourceDrainer implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(ExternalSourceDrainer.class);
        private final CompiledExternalSource source;
        private final ProcessingRuntime runtime;
        private final PrintStream standardError;

        private ExternalSourceDrainer(CompiledExternalSource source, ProcessingRuntime runtime, PrintStream standardError) {
            this.source = source;
            this.runtime = runtime;
            this.standardError = standardError;
        }

        @Override
        public void run() {
            try {
                source.startProcessingEvents(runtime);
            } catch (ProcessingException e) {
                // output it to standard error and the LOG
                standardError.println(e.getLocalizedMessage());
                e.printStackTrace(standardError);

                LOG.error(String.format("Processing exception while draining source [%s]", source), e);

            } catch (Exception e) {
                // output it to standard error and the LOG
                standardError.println(e.getLocalizedMessage());
                e.printStackTrace(standardError);

                LOG.error(String.format("Uncaught exception while draining source [%s]", source), e);

            } finally {
                source.stopProcessingEvents();
            }
        }
    }
}
