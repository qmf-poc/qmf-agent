package qmf.poc.agent.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.Args;
import qmf.poc.agent.broker.Broker;

import java.io.Closeable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSockerProvider implements Closeable {

    private final HttpClient client;
    private final ExecutorService executor;
    private final Broker broker;

    public WebSockerProvider(Broker broker) {
        this.broker = broker;
        log.debug("Building HttpClient...");
        executor = Executors.newCachedThreadPool();
        client = HttpClient.newBuilder().executor(executor).build();
        log.debug("Building HttpClient...done");
    }

    @Override
    public void close() {
        executor.shutdown();
    }


    private WebSocketConnection getConnection(URI uri) throws CompletionException {
        log.debug("Connecting to " + uri + "...");
        final WebSocketHandler listener = new WebSocketHandler(broker);
        final CompletableFuture<WebSocket> future = client.newWebSocketBuilder().buildAsync(uri, listener);
        future.join();
        log.debug("Connecting to " + uri + " done");
        return listener;
    }

    public static void listen(Args args, Broker broker) {
        log.info("Websocket start listening to " + args.serviceUri);
        try (final WebSockerProvider webSockerProvider = new WebSockerProvider(broker)) {
            boolean exit = false;
            while (!exit && !Thread.currentThread().isInterrupted()) {
                try (final WebSocketConnection connection = webSockerProvider.getConnection(args.serviceUri)) {
                    Optional<Throwable> error = connection.listen();
                    if (error.isPresent()) {
                        throw new CompletionException(error.get());
                    }
                } catch (Exception e) {
                    if (e instanceof CompletionException) {
                        log.warn("Connection error: ", e);
                        log.info("Will reconnecting in 2 sec...");
                        //noinspection BusyWait
                        Thread.sleep(2000);
                    }
                    if (e instanceof InterruptedException) {
                        log.warn("Unhandled interrupted exception");
                        // throw new RuntimeException(e);
                    } else {
                        log.error("Unhandled exception: " + e.getMessage(), e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.warn("Unhandled interrupted exception");
            // throw new RuntimeException(e);
        } finally {
            log.info("Websocket stop listening to " + args.serviceUri);
        }
    }

    private static final Log log = LogFactory.getLog("agent");
}
