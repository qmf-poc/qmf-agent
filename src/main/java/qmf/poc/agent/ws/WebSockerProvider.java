package qmf.poc.agent.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.Args;

import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
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

    public WebSockerProvider() {
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
        log.debug("Connecting to {}...", uri.toString());
        final WebSocketListener listener = new WebSocketListener();
        final CompletableFuture<WebSocket> future = client.newWebSocketBuilder().buildAsync(uri, listener);
        future.join();
        log.debug("Connecting to {}... done", uri);
        return listener;
    }

    public static void listen(Args args) {
        try (final WebSockerProvider webSockerProvider = new WebSockerProvider()) {
            boolean exit = false;
            while (!exit && !Thread.currentThread().isInterrupted()) {
                try (final WebSocketConnection connection = webSockerProvider.getConnection(args.serviceUri)) {
                    Optional<Throwable> error = connection.listen();
                    if (error.isPresent()) {
                        throw new CompletionException(error.get());
                    }
                } catch (Exception e) {
                    if (e instanceof CompletionException) {
                        final Throwable c = e.getCause() == null ? e : e.getCause();
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
        }
    }

    private static final Logger log = LoggerFactory.getLogger("agent");
}
