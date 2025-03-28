package qmf.poc.agent.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.broker.Broker;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.min;

public class WebSocketHandler implements WebSocket.Listener, WebSocketConnection {
    // use StringBuffer over StringBuilder for thread safety
    private final StringBuffer accumulatedText = new StringBuffer();
    private final CompletableFuture<Optional<Throwable>> closeFuture = new CompletableFuture<>();
    private final AtomicReference<WebSocket> ws = new AtomicReference<>();
    private final Broker broker;

    public WebSocketHandler(Broker broker) {
        this.broker = broker;
    }

    public Optional<Throwable> listen() {
        return closeFuture.join();
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        log.trace("WebSocketListener.onOpen, subProtocol=" + webSocket.getSubprotocol());
        ws.set(webSocket);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        accumulatedText.append(data);
        log.debug("WebSocketListener.onText, last=" + last + ", " + accumulatedText);
        if (last) {
            final String response;
            try {
                response = broker.handleJsonRPC(accumulatedText.toString());
                if (response != null) {
                    log.debug("WebSocketListener.sendText, response=" + response.substring(0, min(200, response.length())));
                    webSocket.sendText(response, true);
                }
            } catch (Exception e) {
                log.warn("Failed to parse JSON-RPC message", e);
            }
            accumulatedText.setLength(0);
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        log.trace("WebSocketListener.onBinary, last=" + last);
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        log.trace("WebSocketListener.onPing, message=" + message);
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        log.trace("WebSocketListener.onPong, message" + message);
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        log.trace("WebSocketListener.onClose, code=" + statusCode + ", reason=" + reason);
        closeFuture.complete(Optional.empty());
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.error("WebSocketListener.onError", error);
        WebSocket.Listener.super.onError(webSocket, error);
        if (error instanceof InterruptedException) {
            webSocket.sendClose(1, "Interrupted");
            closeFuture.complete(Optional.empty());
        } else {
            closeFuture.complete(Optional.of(error));
        }
    }

    @Override
    public void close() {
        final WebSocket webSocket = ws.get();
        if (webSocket != null) {
            webSocket.sendClose(2, "External close (should never happen)");
        }
    }

    private static final Log log = LogFactory.getLog("agent");
}
