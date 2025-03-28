package qmf.poc.agent.jsonrpc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class JsonRpc {
    private final Gson gson = new Gson();

    public Map<String, Object> parse(String message) {
        return gson.fromJson(message, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public String formatResult(Integer id, Object result) {
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result", result);
        return gson.toJson(response);
    }

    public String formatResult(Integer id, String result) {
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result", result);
        return gson.toJson(response);
    }

    public String formatError(Integer id, int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        response.put("error", error);

        return gson.toJson(response);
    }
}
