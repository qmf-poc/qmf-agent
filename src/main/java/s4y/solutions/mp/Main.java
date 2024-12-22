package s4y.solutions.mp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.agent.Agent;
import s4y.solutions.mp.components.connections_pool.naive.NaiveConnectionsPool;
import s4y.solutions.mp.components.http.sun.SunEndpoint;
import s4y.solutions.mp.components.http.sun.SunHttpServer;
import s4y.solutions.mp.components.http.sun.handlers.CatalogHandler;
import s4y.solutions.mp.components.http.sun.handlers.PingHandler;
import s4y.solutions.mp.components.qmf_catalog.db2.Db2CatalogProvider;
import s4y.solutions.mp.errorhandler.DefaultErrorHandler;
import s4y.solutions.mp.errorhandler.IErrorHandler;
import s4y.solutions.mp.workflows.Workflow;

import static java.lang.System.exit;

/**
 * The main class of the application.
 */
public class Main {

    /**
     * The entry point of the application.
     * It configures the components of the application and runs the agent.
     * <p>
     * The agent is a composition of the HTTP server and the connection pool.
     * <p>
     * The HTTP server serves the catalog of the DB2 database through the given connection pool.
     * <p>
     * The connection pool is a pool of connections to the DB2 database.
     * <p>
     * The connection pool and HTTP server are configured by the command line, environment variables, and properties.
     * <p>
     * The agent is run and the exit code is returned.
     *
     * @param cli the command line arguments
     */
    public static void main(String[] cli) {
        // Preamble
        String jvmVersion = System.getProperty("java.version");
        logger.info("JVM Version: {}", jvmVersion);

        IErrorHandler errorHandler = new DefaultErrorHandler();

        int code = Workflow
                .setAgentDefinition(() ->
                        defineAgent(errorHandler, cli))
                .withAgent()
                .checkPreconditions(agent ->
                        agent.checkConnectionPool() ? 0 : 1)
                .ifOk(agent ->
                        agent.runHttpServer() ? 0 : 126)
                .waitForCtrlC(agent->{
                        logger.debug("Shutting down...");
                        agent.close();
                        logger.debug("Shut down");
                });
        exit(code);
    }

    static Agent.Definition<
            SunHttpServer,
            SunHttpServer.Definition<
                    Db2CatalogProvider<NaiveConnectionsPool>,
                    Db2CatalogProvider.Definition<NaiveConnectionsPool, NaiveConnectionsPool.Definition>>,
            NaiveConnectionsPool,
            NaiveConnectionsPool.Definition>
    defineAgent(IErrorHandler errorHandler, String[] cli) {
        return Agent.define(errorHandler, agent -> {
                    // define an implementation out of a pool of connections to a DB2 database
                    // it will be passed down to the other components (HTTP server for.ex.)
                    agent.connectionPool(() -> NaiveConnectionsPool.define(
                            // use the given error handler
                            errorHandler,
                            // and be configured (by effects) from command line, environment variables, and properties
                            // as they will have been set at the moment of the agent's creation
                            pool -> {
                                pool.db2cs(arg -> {
                                    arg.cli(cli);
                                    arg.shortArg("c");
                                    arg.longArg("db2_connection_string");
                                    arg.env("DB2_CONNECTION_STRING");
                                    arg.property("db2.connection.string");
                                    arg.defaultValue(null);
                                });
                                pool.db2user(arg -> {
                                    arg.cli(cli);
                                    arg.shortArg("u");
                                    arg.longArg("db2_user");
                                    arg.env("DB2_USER");
                                    arg.property("db2.user");
                                });
                                pool.db2password(arg -> {
                                    arg.cli(cli);
                                    arg.shortArg("w");
                                    arg.longArg("db2_password");
                                    arg.env("DB2_PASSWORD");
                                    arg.property("db2.password");
                                });
                            }));
                    // define an implementation out of an HTTP server
                    agent.httpServer(connectionPool ->
                            SunHttpServer.define(errorHandler, server -> {
                                // listen to the port configured by the user through
                                // the command line, environment variables, or properties
                                server.port(params -> {
                                    params.cli(cli);
                                    params.shortArg("p");
                                    params.longArg("http_port");
                                    params.env("HTTP_PORT");
                                    params.property("http.port");
                                    params.defaultValue(0);
                                });
                                // use the catalog provider to serve the catalog
                                // through the given connection pool
                                server.catalogProvider(() -> Db2CatalogProvider.define(errorHandler, provider ->
                                        provider.connectionPool(() -> connectionPool)));
                                server.endpoint(ignored ->
                                        new SunEndpoint("/ping", new PingHandler(errorHandler)));
                                server.endpoint(provider ->
                                        new SunEndpoint("/catalog", new CatalogHandler(provider.use(), errorHandler)));
                            }));
                }
        );

    }

    static final Logger logger = LoggerFactory.getLogger(Main.class);
}
