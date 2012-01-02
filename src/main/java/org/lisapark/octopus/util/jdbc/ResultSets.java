package org.lisapark.octopus.util.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ResultSets {
    private static final Logger LOG = LoggerFactory.getLogger(Connections.class);

    public static void closeQuietly(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOG.warn("Problem closing resultSet", e);
            }
        }
    }
}
