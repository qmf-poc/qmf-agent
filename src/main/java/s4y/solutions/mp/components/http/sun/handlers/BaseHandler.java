package s4y.solutions.mp.components.http.sun.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

abstract class BaseHandler  implements HttpHandler {
    protected final IErrorHandler errorHandler;

    BaseHandler(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;

    }
    Map<String, String> queryParams(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        String queryString = requestURI.getQuery();
        Map<String, String> queryParams = new HashMap<>();

        if (queryString != null) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    try {
                        queryParams.put(keyValue[0], URLDecoder.decode( keyValue[1], "UTF-8"));
                    } catch (Exception e) {
                        errorHandler.handleError(e);
                    }
                }
            }
        }

        return queryParams;
    }
    /** {@inheritDoc} */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            handleSecure(exchange);
        } catch (Exception e) {
            errorHandler.handleError(e);
            byte[] jsonResponse = e.getMessage().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, jsonResponse.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse);
            }
        }
    }

    void handleSecure(HttpExchange exchange) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    };
}
