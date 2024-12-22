package s4y.solutions.mp.components.connections_pool.naive;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.components.args.ArgumentString;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

// naive implementation of a connection pool
/**
 * <p>NaiveConnectionsPool class.</p>
 *
 * @author dsa
 */
public class NaiveConnectionsPool implements IConnectionsPool {

    private final String db2user;
    private final String db2password;
    private final String db2cs;
    private final IErrorHandler errorHandler;
    private Connection connection;

    private NaiveConnectionsPool(String db2user, String db2password, String db2cs, IErrorHandler errorHandler) {
        this.db2user = db2user;
        this.db2password = db2password;
        this.db2cs = db2cs;
        this.errorHandler = errorHandler;
        logger.debug("Instantiated");
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        logger.debug("Closing...");
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                if (errorHandler != null) {
                    errorHandler.handleError(e);
                }
            }
        }
        logger.info("Closed");
    }

    /** {@inheritDoc} */
    @Override
    public Connection getConnection() {
        try {
            if (connection == null) {
                connection = java.sql.DriverManager.getConnection(db2cs, db2user, db2password);
            }
            return connection;
        } catch (SQLException e) {
            if (errorHandler != null) {
                errorHandler.handleError(e);
            }
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean testConnectionString() {
        try (Connection ignored = java.sql.DriverManager.getConnection(db2cs, db2user, db2password)) {
            return true;
        } catch (SQLException e) {
            if (errorHandler != null) {
                errorHandler.handleError(e);
            }
            return false;
        }
    }

    /**
     * <p>define.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     * @param modifier a {@link java.util.function.Consumer} object
     * @return a {@link s4y.solutions.mp.components.connections_pool.naive.NaiveConnectionsPool.Definition} object
     */
    public static Definition define(IErrorHandler errorHandler, Consumer<Definition.DSL> modifier) {
        final Definition definition = new Definition(errorHandler);
        modifier.accept(definition.dsl);
        return definition;
    }

    public static class Definition implements IConnectionsPool.Definition<NaiveConnectionsPool> {
        private final ArgumentString db2user = new ArgumentString();
        private final ArgumentString db2password = new ArgumentString();
        private final ArgumentString db2cs = new ArgumentString();
        private final IErrorHandler errorHandler;

        private Definition(IErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
        }

        static NaiveConnectionsPool singleton;
        public NaiveConnectionsPool use() {
            if (singleton == null) {
                singleton = new NaiveConnectionsPool(
                        db2user.use(),
                        db2password.use(),
                        db2cs.use(),
                        errorHandler
                );
            }
            return singleton;
        }

        private final DSL dsl = new DSL();

        public class DSL {
            public void db2user(@NotNull Consumer<ArgumentString.DSL> modifier) {
                modifier.accept(db2user.dsl);
                logger.debug("configured with the user: {}", db2user.use());
            }

            public void db2password(@NotNull Consumer<ArgumentString.DSL> modifier) {
                modifier.accept(db2password.dsl);
                logger.debug("configured with the password: *****");
            }

            public void db2cs(@NotNull Consumer<ArgumentString.DSL> modifier) {
                modifier.accept(db2cs.dsl);
                logger.debug("configured with the connection string: {}", db2cs.use());
            }
        }
    }
    static final Logger logger = LoggerFactory.getLogger(NaiveConnectionsPool.class);
}
