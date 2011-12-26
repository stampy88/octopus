package org.matrixlab.octopus.util.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Connections {
    private static final Logger LOG = LoggerFactory.getLogger(Connections.class);

    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.warn("Problem closing connection", e);
            }
        }
    }
}
