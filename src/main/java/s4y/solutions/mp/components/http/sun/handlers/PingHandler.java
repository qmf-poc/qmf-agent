package s4y.solutions.mp.components.http.sun.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

/**
 * <p>PingHandler class.</p>
 *
 * @author dsa
 */
public class PingHandler extends BaseHandler {
    /**
     * <p>Constructor for PingHandler.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     */
    public PingHandler(IErrorHandler errorHandler) {
        super(errorHandler);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = queryParams(exchange);
        logger.debug("Handling ping request");

        String payload =  params.get("payload");
        String pong = payload == null ? "pong" : "pong " + payload;

        String jsonResponse = "{\"pong\": \"" + pong + "\"}";

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    static final Logger logger = LoggerFactory.getLogger(PingHandler.class.getName());
}
