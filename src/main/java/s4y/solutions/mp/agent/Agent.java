package s4y.solutions.mp.agent;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.http.IHttpServer;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// PROBLEM: agent does not instantiate the http server and the connections' pool,
//          but it is responsible for their closing
/**
 * <p>Agent class.</p>
 *
 * @author dsa
 */
public class Agent<S extends IHttpServer, CPL extends IConnectionsPool> implements AutoCloseable {
    private final CPL connectionsPool;
    private final IErrorHandler errorHandler;
    private final S httpServer;

    private Agent(IErrorHandler errorHandler, CPL connectionsPool, S httpServer) {
        this.connectionsPool = connectionsPool;
        this.errorHandler = errorHandler;
        this.httpServer = httpServer;
        logger.debug("Instantiated");
    }

    public boolean checkConnectionPool() {
        logger.debug("Checking connection pool...");
        if(connectionsPool.testConnectionString()) {
            logger.debug("Connection pool is ok");
            return true;
        } else {
            logger.debug("Connection pool is not ok");
            return false;
        }
    }

    public boolean runHttpServer() {
        boolean result = httpServer.start();
        logger.debug("HTTP server is running");
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        logger.debug("Shutting down");
        try {
            connectionsPool.close();
        } catch (Exception e) {
            errorHandler.handleError(e);
        }
        try {
            httpServer.close();
        } catch (Exception e) {
            errorHandler.handleError(e);
        }
        logger.info("Shut down");
    }

    /**
     * <p>define.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     * @param modifier a {@link java.util.function.Consumer} object
     * @param <S> a S class
     * @param <D_S> a D_S class
     * @param <CPL> a CPL class
     * @param <D_CPL> a D_CPL class
     * @return a {@link s4y.solutions.mp.agent.Agent.Definition} object
     */
    @NotNull
    public static <
            S extends IHttpServer, D_S extends IHttpServer.Definition<S>,
            CPL extends IConnectionsPool, D_CPL extends IConnectionsPool.Definition<CPL>>
    Definition<S, D_S, CPL, D_CPL> define(IErrorHandler errorHandler, Consumer<Definition<S, D_S, CPL, D_CPL>.DSL> modifier) {
        final Definition<S, D_S, CPL, D_CPL> definition = new Definition<>(errorHandler);
        modifier.accept(definition.dsl);
        return definition;
    }

    public static class Definition<
            S extends IHttpServer, D_S extends IHttpServer.Definition<S>,
            CPL extends IConnectionsPool, D_CPL extends IConnectionsPool.Definition<CPL>> {
        private D_CPL connectionPoolDefinition;
        private D_S httpServerDefinition;
        private final IErrorHandler errorHandler;

        private Agent<S, CPL> singleton;

        public Definition(IErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
        }

        public Agent<S, CPL> use() {
            if (singleton == null) {
                CPL connectionsPool = connectionPoolDefinition.use();
                if (connectionsPool == null) return null;
                S httpServer = httpServerDefinition.use();
                if (httpServer == null) return null;
                singleton = new Agent<>(errorHandler, connectionsPool, httpServer);
            }
            return singleton;
        }

        private final DSL dsl = new DSL();

        public class DSL {
            public void connectionPool(@NotNull Supplier<D_CPL> init) {
                connectionPoolDefinition = init.get();
                logger.debug("configured with the connection pool: {}",
                        connectionPoolDefinition.getClass());
            }

            public void httpServer(@NotNull Function<D_CPL, D_S> init) {
                httpServerDefinition = init.apply(connectionPoolDefinition);
                logger.debug("configured with the HTTP server: {}",
                        httpServerDefinition.getClass());
            }
        }
    }

    static final Logger logger = LoggerFactory.getLogger(Agent.class);
}
