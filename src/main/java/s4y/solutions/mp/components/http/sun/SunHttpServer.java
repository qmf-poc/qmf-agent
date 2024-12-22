package s4y.solutions.mp.components.http.sun;

import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import s4y.solutions.mp.components.args.ArgumentInt;
import s4y.solutions.mp.components.http.IHttpServer;
import s4y.solutions.mp.components.qmf_catalog.ICatalogProvider;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>SunHttpServer class.</p>
 *
 * @author dsa
 */
public class SunHttpServer implements IHttpServer {
    private final HttpServer server;

    SunHttpServer(HttpServer server) {
        this.server = server;
        logger.debug("Instantiated");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean start() {
        server.setExecutor(null);
        server.start();
        logger.info("Server started on port {}", server.getAddress().getPort());
        return true;
    }

    @Override
    public void stop() {
        server.stop(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        logger.debug("Shutting down...");
        server.stop(0);
        logger.info("Shut down");
    }

    /**
     * <p>define.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     * @param modifier     a {@link java.util.function.Consumer} object
     * @param <CPR>        a CPR class
     * @param <D_CPR>      a D_CPR class
     * @return a {@link s4y.solutions.mp.components.http.sun.SunHttpServer.Definition} object
     */
    public static <CPR extends ICatalogProvider, D_CPR extends ICatalogProvider.Definition<CPR>>
    Definition<CPR, D_CPR> define(IErrorHandler errorHandler, Consumer<Definition<CPR, D_CPR>.DSL> modifier) {
        final Definition<CPR, D_CPR> definition = new Definition<>(errorHandler);
        modifier.accept(definition.dsl);
        return definition;
    }

    // PROBLEM: catalog provider does not have a clearly defined owner;
    //          it is intended to be used by the handlers,
    //          and it is not determined how many instances
    //          of the catalog provider will be created, thus
    //          it is not clear how to close them
    // WORKAROUND: create once within the http server and close it when the server is closed
    //          pros: the instance to be created if none handlers require it
    // TODO:  current status - to do not close the catalog provider at all
    public static class Definition<CPR extends ICatalogProvider, D_CPR extends ICatalogProvider.Definition<CPR>>
            implements IHttpServer.Definition<SunHttpServer> {
        private final List<Supplier<SunEndpoint>> endpoints = new LinkedList<>();
        private final IErrorHandler errorHandler;
        private final ArgumentInt port;
        private D_CPR catalogProviderDefinition;

        public Definition(IErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            this.port = new ArgumentInt(errorHandler);
        }

        private SunHttpServer singleton;

        public SunHttpServer use() {
            if (singleton == null) {
                try {
                    HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port.use()), 0);
                    for (Supplier<SunEndpoint> endpoint : endpoints) {
                        SunEndpoint ep = endpoint.get();
                        server.createContext(ep.path(), ep.handler());
                        logger.debug("add the context: {}", ep.path());
                    }

                    singleton = new SunHttpServer(server);
                } catch (IOException e) {
                    if (errorHandler != null) {
                        errorHandler.handleError(new Error("Can't create http server", e));
                    }
                }
            }
            return singleton;
        }

        private final DSL dsl = new DSL();

        public class DSL {

            public void port(@NotNull Consumer<ArgumentInt.DSL> modifier) {
                modifier.accept(port.dsl);
                logger.debug("configured to listen on port {}", port.use());
            }

            public void catalogProvider(@NotNull Supplier<D_CPR> init) {
                catalogProviderDefinition = init.get();
                logger.debug("configured with the catalog provider: {}",
                        catalogProviderDefinition.getClass());
            }

            public void endpoint(@NotNull Function<D_CPR, SunEndpoint> creator) {
                Supplier<SunEndpoint> ep = () -> creator.apply(catalogProviderDefinition);
                endpoints.add(ep);
            }
        }
    }

    static final Logger logger = LoggerFactory.getLogger(SunHttpServer.class);
}
