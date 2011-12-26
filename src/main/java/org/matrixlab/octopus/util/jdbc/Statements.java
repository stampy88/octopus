package org.matrixlab.octopus.util.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Statements {

    private static final Logger LOG = LoggerFactory.getLogger(Connections.class);

    public static void closeQuietly(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOG.warn("Problem closing statement", e);
            }
        }
    }
}
