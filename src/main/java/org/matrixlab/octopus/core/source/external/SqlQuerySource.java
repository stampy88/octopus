package org.matrixlab.octopus.core.source.external;

import com.google.common.collect.Maps;
import org.matrixlab.octopus.core.AbstractNode;
import org.matrixlab.octopus.core.ProcessingException;
import org.matrixlab.octopus.core.ValidationException;
import org.matrixlab.octopus.core.event.Attribute;
import org.matrixlab.octopus.core.event.Event;
import org.matrixlab.octopus.core.event.EventType;
import org.matrixlab.octopus.core.processor.parameter.Constraints;
import org.matrixlab.octopus.core.processor.parameter.Parameter;
import org.matrixlab.octopus.core.runtime.ProcessingRuntime;
import org.matrixlab.octopus.util.Booleans;
import org.matrixlab.octopus.util.jdbc.Connections;
import org.matrixlab.octopus.util.jdbc.ResultSets;
import org.matrixlab.octopus.util.jdbc.Statements;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

/**
 * This class is an {@link ExternalSource} that is used to access relational databases. It can be configured with
 * a JDBC Url for the database, username, password, Driver fully qualified class name, and a query to execute.
 * <p/>
 * Currently, the source uses the {@link #getOutputEventType()} to get the names of the columns and types of the columns,
 * but it will probably be changed in the future to support a mapper that takes a {@link ResultSet} and produces an
 * {@link Event}.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class SqlQuerySource extends AbstractNode implements ExternalSource {
    private static final String DEFAULT_NAME = "JDBC";
    private static final String DEFAULT_DESCRIPTION = "JDBC Source for events";

    public static final int URL_PARAMETER_ID = 1;
    public static final int USER_NAME_PARAMETER_ID = 2;
    public static final int PASSWORD_PARAMETER_ID = 3;
    public static final int DRIVER_PARAMETER_ID = 4;
    public static final int QUERY_PARAMETER_ID = 5;

    private SqlQuerySource(UUID sourceId, String name, String description) {
        super(sourceId, name, description);
    }

    private SqlQuerySource(UUID sourceId, SqlQuerySource copyFromSource) {
        super(sourceId, copyFromSource);
    }

    private SqlQuerySource(SqlQuerySource copyFromSource) {
        super(copyFromSource);
    }

    @SuppressWarnings("unchecked")
    public void setUrl(String url) {
        getParameter(URL_PARAMETER_ID).setValue(url);
    }

    @SuppressWarnings("unchecked")
    public void setUsername(String url) {
        getParameter(USER_NAME_PARAMETER_ID).setValue(url);
    }

    @SuppressWarnings("unchecked")
    public void setPassword(String url) {
        getParameter(PASSWORD_PARAMETER_ID).setValue(url);
    }

    @SuppressWarnings("unchecked")
    public void setDriverClass(String url) {
        getParameter(DRIVER_PARAMETER_ID).setValue(url);
    }

    @SuppressWarnings("unchecked")
    public void setQuery(String url) {
        getParameter(QUERY_PARAMETER_ID).setValue(url);
    }

    @Override
    public EventType getOutputEventType() {
        // todo 
        return null;
    }

    @Override
    public SqlQuerySource newInstance() {
        UUID sourceId = UUID.randomUUID();
        return new SqlQuerySource(sourceId, this);
    }

    @Override
    public SqlQuerySource copyOf() {
        return new SqlQuerySource(this);
    }

    public static SqlQuerySource newTemplate() {
        UUID processorId = UUID.randomUUID();
        SqlQuerySource jdbc = new SqlQuerySource(processorId, DEFAULT_NAME, DEFAULT_DESCRIPTION);

        jdbc.addParameter(Parameter.stringParameterWithIdAndName(URL_PARAMETER_ID, "URL").required(true));
        jdbc.addParameter(Parameter.stringParameterWithIdAndName(USER_NAME_PARAMETER_ID, "User name"));
        jdbc.addParameter(Parameter.stringParameterWithIdAndName(PASSWORD_PARAMETER_ID, "Password"));
        jdbc.addParameter(Parameter.stringParameterWithIdAndName(DRIVER_PARAMETER_ID, "Driver Class").required(true).
                constraint(Constraints.classConstraintWithMessage("%s is not a valid Driver Class")));
        jdbc.addParameter(Parameter.stringParameterWithIdAndName(QUERY_PARAMETER_ID, "Query").required(true));

        return jdbc;
    }

    @Override
    public CompiledExternalSource compile() throws ValidationException {
        // todo validate
        return new CompiledSqlQuerySource(
                getParameterValueAsString(URL_PARAMETER_ID),
                getParameterValueAsString(USER_NAME_PARAMETER_ID),
                getParameterValueAsString(PASSWORD_PARAMETER_ID),
                getParameterValueAsString(DRIVER_PARAMETER_ID),
                getParameterValueAsString(QUERY_PARAMETER_ID),
                getOutputEventType()
        );
    }

    private static class CompiledSqlQuerySource implements CompiledExternalSource {
        private final String url;
        private final String userName;
        private final String password;
        private final String className;
        private final String query;
        private final EventType eventType;

        private volatile boolean running;

        public CompiledSqlQuerySource(String url, String userName, String password, String className,
                                      String query, EventType eventType) {
            this.url = url;
            this.userName = userName;
            this.password = password;
            this.className = className;
            this.query = query;
            this.eventType = eventType;
        }

        @Override
        public void startProcessingEvents(ProcessingRuntime runtime) {
            checkState(!running, "Source is already processing events. Cannot call processEvents again");
            Connection connection = getConnection(className, url, userName, password);
            Statement statement = null;
            ResultSet rs = null;
            try {
                statement = connection.createStatement();

                rs = statement.executeQuery(query);
                processResultSet(rs, runtime);
            } catch (SQLException e) {
                throw new ProcessingException("Problem processing result set from database. Please check your settings.", e);

            } finally {
                ResultSets.closeQuietly(rs);
                Statements.closeQuietly(statement);
                Connections.closeQuietly(connection);
            }
        }

        void processResultSet(ResultSet rs, ProcessingRuntime runtime) throws SQLException {
            Thread thread = Thread.currentThread();

            while (!thread.isInterrupted() && running && rs.next()) {
                Event newEvent = createEventFromResultSet(rs, eventType);

                runtime.sendEvent(newEvent, eventType);
            }
        }

        @Override
        public void stopProcessingEvents() {
            this.running = false;
        }

        Connection getConnection(String className, String url, String userName, String password) {

            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                // this should never happen since the parameter is constrained
                throw new ProcessingException("Could not find JDBC Driver Class " + className, e);
            }

            Connection connection;

            try {
                if (userName == null && password == null) {
                    connection = DriverManager.getConnection(url);
                } else {
                    connection = DriverManager.getConnection(url, userName, password);
                }
            } catch (SQLException e) {
                throw new ProcessingException("Could not connect to database. Please check your settings.", e);
            }

            return connection;
        }

        Event createEventFromResultSet(ResultSet rs, EventType eventType) throws SQLException {
            Map<String, Object> attributeValues = Maps.newHashMap();

            for (Attribute attribute : eventType.getAttributes()) {
                Class type = attribute.getType();
                String attributeName = attribute.getName();

                if (type == String.class) {
                    String value = rs.getString(attributeName);
                    attributeValues.put(attributeName, value);

                } else if (type == Integer.class) {
                    int value = rs.getInt(attributeName);
                    attributeValues.put(attributeName, value);

                } else if (type == Short.class) {
                    short value = rs.getShort(attributeName);
                    attributeValues.put(attributeName, value);

                } else if (type == Long.class) {
                    long value = rs.getLong(attributeName);
                    attributeValues.put(attributeName, value);

                } else if (type == Double.class) {
                    double value = rs.getDouble(attributeName);
                    attributeValues.put(attributeName, value);

                } else if (type == Float.class) {
                    float value = rs.getFloat(attributeName);
                    attributeValues.put(attributeName, value);

                } else if (type == Boolean.class) {
                    String value = rs.getString(attributeName);
                    attributeValues.put(attributeName, Booleans.parseBoolean(value));
                } else {
                    throw new IllegalArgumentException(String.format("Unknown attribute type %s", type));
                }
            }

            return new Event(attributeValues);
        }
    }
}
