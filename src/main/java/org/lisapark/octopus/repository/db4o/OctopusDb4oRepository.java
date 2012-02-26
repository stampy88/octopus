package org.lisapark.octopus.repository.db4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ext.Db4oException;
import com.db4o.query.Predicate;
import com.db4o.ta.DeactivatingRollbackStrategy;
import com.db4o.ta.TransparentActivationSupport;
import com.db4o.ta.TransparentPersistenceSupport;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.repository.AbstractOctopusRepository;
import org.lisapark.octopus.repository.OctopusRepository;
import org.lisapark.octopus.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class OctopusDb4oRepository extends AbstractOctopusRepository implements OctopusRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OctopusDb4oRepository.class);

    /**
     * This is the actual DB4O container that will do the work of persisting and retrieving objects.
     */
    private ObjectContainer container;
    private final String fileName;

    public OctopusDb4oRepository(String fileName) {
        this.fileName = fileName;
        this.container = ensureObjectContainerIsOpen();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (container != null) {
                    container.close();
                }
            }
        });
    }

    private ObjectContainer ensureObjectContainerIsOpen() {
        if (container == null) {
            EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
            config.common().add(new TransparentActivationSupport());
            config.common().add(new TransparentPersistenceSupport(new DeactivatingRollbackStrategy()));

            container = Db4oEmbedded.openFile(config, fileName);
        }

        return container;
    }

    private void rollbackCloseAndClearObjectContainer() {
        if (container != null) {
            container.rollback();

            container.close();
            container = null;
        }
    }

    @Override
    public synchronized void saveProcessingModel(ProcessingModel model) throws RepositoryException {
        checkArgument(model != null, "model cannot be null");

        model.setLastSaved(DateTime.now());

        try {
            ensureObjectContainerIsOpen();

            LOG.debug("Saving model {}...", model.getModelName());

            container.store(model);
            container.commit();

            LOG.debug("Saving completed");
        } catch (Db4oException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public synchronized List<ProcessingModel> getProcessingModelsByName(String name) throws RepositoryException {
        checkArgument(name != null, "name cannot be null");

        final String query;

        if (name.trim().length() == 0) {
            query = ".*";

        } else {
            // make sure that the search string always ends in a '*' 
            if (name.endsWith("*")) {
                // then change from the "regular" '*' to the regex friendly '.*'
                query = StringUtils.replace(name, "*", ".*");

            } else {
                query = StringUtils.replace(name, "*", ".*") + ".*";
            }
        }

        LOG.debug("Getting models like {}", query);

        try {
            ensureObjectContainerIsOpen();

            return container.query(new Predicate<ProcessingModel>() {
                public boolean match(ProcessingModel model) {
                    return model.getModelName().matches(query);
                }
            });
        } catch (Db4oException e) {
            throw new RepositoryException(e);
        } catch (IllegalStateException e) {
            // this is thrown if the class structure is incompatible with the current structure
            throw new RepositoryException(e);
        }
    }
}
