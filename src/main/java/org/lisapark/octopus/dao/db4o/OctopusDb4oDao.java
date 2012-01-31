package org.lisapark.octopus.dao.db4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import org.apache.commons.lang.StringUtils;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.dao.OctopusDao;

import java.util.List;

import static com.jgoodies.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class OctopusDb4oDao implements OctopusDao {

    private ObjectContainer db;

    public OctopusDb4oDao(String fileName) {
        db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), fileName);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (db != null) {
                    db.close();
                }
            }
        });
    }

    @Override
    public void storeProcessingModel(ProcessingModel model) {
        checkArgument(model != null, "model cannot be null");

        db.store(model);
    }

    @Override
    public List<ProcessingModel> getProcessingModelsByName(String name) {
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

        return db.query(new Predicate<ProcessingModel>() {
            public boolean match(ProcessingModel model) {
                return model.getModelName().matches(query);
            }
        });
    }

    public static void listResult(List<?> result) {
        System.out.println(result.size());
        for (Object o : result) {
            System.out.println(o);
        }
    }
}
