package qmf.poc.agent.broker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Broker {
    private ConcurrentHashMap<Double, CompletableFuture<Double>> pendingRequests = new ConcurrentHashMap<>();

    public String handleJsonRPC(String message) {
        try {
            final Map<String, Object> json = messageToJSON(message);
            final Double id = (Double) json.get("id");
            try {
                if (json.containsKey("method")) {
                    return handleMethod(id, json);
                } else if (json.containsKey("result")) {
                    return handleResult(id, json);
                } else {
                    throw new ParseException("JSON-RPC must contain either method or result", 0);
                }
            } catch (Exception e) {
                log.warn("Failed to handle JSON-RPC message", e);
                return errorJson(id, 2, e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Failed to parse JSON-RPC message", e);
            return errorJson(null, 1, e.getMessage());
        }
    }

    private String handleMethod(Double id, Map<String, Object> json) throws ParseException {
        final String method = (String) json.get("method");
        switch (method) {
            case "ping":
                return handlePing(id, json);
            default:
                throw new ParseException("Unknown method: " + method, 0);
        }
    }

    private String handlePing(Double id, Map<String, Object> json) {
        final String payload = (String) json.get("params");
        return responseJson(id, payload);
    }

    private String handleResult(Double id, Map<String, Object> json) {
        return "result";
    }

    private Map<String, Object> messageToJSON(String message) {
        return parseJson(message);
    }

    ;

    private final static Gson gson = new Gson();

    private static Map<String, Object> parseJson(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    private static String responseJson(Double id, String result) {
        final Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result", result);
        return gson.toJson(response);
    }

    private static String errorJson(Double id, int code, String message) {
        final Map<String, Object> response = Map.of(
                "jsonrpc", "2.0",
                "id", id,
                "error", Map.of("code", code, "message", message));
        return gson.toJson(response);
    }

    private static final Log log = LogFactory.getLog("agent");
}
