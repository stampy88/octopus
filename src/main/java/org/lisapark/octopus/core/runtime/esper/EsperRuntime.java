package org.lisapark.octopus.core.runtime.esper;

import com.espertech.esper.client.EPServiceProvider;
import org.lisapark.octopus.core.event.Event;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.source.Source;
import org.lisapark.octopus.core.source.external.CompiledExternalSource;
import org.lisapark.octopus.util.esper.EsperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    static enum State {
        NOT_STARTED, RUNNING, SHUTDOWN
    }

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private State currentState = State.NOT_STARTED;

    public EsperRuntime(EPServiceProvider epService, Collection<CompiledExternalSource> externalSources) {
        checkArgument(epService != null, "epService cannot be null");
        checkArgument(externalSources != null, "externalSources cannot be null");
        this.epService = epService;
        this.externalSources = externalSources;
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
                executorService.submit(new ExternalSourceDrainer(source, this));
            }
        } finally {
            writeLock.unlock();
        }

        executorService.shutdown();
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

        public ExternalSourceDrainer(CompiledExternalSource source, ProcessingRuntime runtime) {
            this.source = source;
            this.runtime = runtime;
        }

        @Override
        public void run() {
            try {
                source.startProcessingEvents(runtime);
            } catch (Exception e) {
                LOG.error(String.format("Uncaught exception while draining source [%s]", source), e);

            } finally {
                source.stopProcessingEvents();
            }
        }
    }
}
