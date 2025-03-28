package qmf.poc.agent.broker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.catalog.models.Catalog;
import qmf.poc.agent.jsonrpc.JsonRpc;
import qmf.poc.agent.run.QMFObjectRunner;

import java.text.ParseException;
import java.util.Map;

import static java.lang.Math.min;

public class Broker {
    private final CatalogProvider catalogProvider;
    private final QMFObjectRunner qmfObjectRunner;
    private final JsonRpc jsonRpc = new JsonRpc();

    public Broker(CatalogProvider catalogProvider, QMFObjectRunner qmfObjectRunner) {
        this.catalogProvider = catalogProvider;
        this.qmfObjectRunner = qmfObjectRunner;
    }

    private String handlePing(Integer id, Map<String, Object> params) {
        String payload = params.get("payload") == null ? "null" : params.get("payload").toString();
        log.debug("handleMethod: ping, id=" + id + ", payload=" + payload);
        String result = "pong: " + payload;
        log.debug("handled: snapshot, id=" + id + ", result=\"" + result + "\"");
        return jsonRpc.formatResult(id, result);
    }

    private String handleSnapshot(Integer id) {
        log.debug("method: snapshot, id=" + id);
        // TODO: handle exception join does not return?
        final Catalog catalog = catalogProvider.catalogParallel().join();
        if (log.isDebugEnabled()) {
            String res = catalog.toString();
            log.debug("handled: snapshot, id=" + id + ", result=\"" + res.substring(0, min(200, res.length())) + "\"");
        }
        return jsonRpc.formatResult(id, Map.of("catalog", catalog));
    }

    private String handleRun(Integer id, Map<String, Object> params) throws Exception {
        log.debug("method: run, id=" + id);

        String owner = (String) params.get("owner");
        if (owner == null) {
            throw new ParseException("Missing owner parameter", 0);
        }
        String name = (String) params.get("name");
        if (name == null) {
            throw new ParseException("Missing name parameter", 0);
        }

        final String result = qmfObjectRunner.retrieveObjectHTML(owner, name, "html");

        if (log.isDebugEnabled()) {
            log.debug("handled: run, id=" + id + ", result=\"" + result.substring(0, min(200, result.length())) + "\"");
        }
        return jsonRpc.formatResult(id, Map.of("body", result, "owner", owner, "name", name));
    }

    public String handleJsonRPC(String message) {
        try {
            final Map<String, Object> json = jsonRpc.parse(message);
            final Double idd = (Double) json.get("id");
            final Integer id = idd == null ? null : idd.intValue();

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
                return jsonRpc.formatError(id, 2, e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Failed to parse JSON-RPC message", e);
            return jsonRpc.formatError(null, 1, e.getMessage());
        }
    }

    private String handleMethod(Integer id, Map<String, Object> json) throws Exception {
        final String method = (String) json.get("method");
        switch (method) {
            case "ping":
                return handlePing(id, getParams(json));
            case "snapshot":
                return handleSnapshot(id);
            case "run":
                return handleRun(id, getParams(json));
            default:
                throw new ParseException("Unknown method: " + method, 0);
        }
    }

    private Map<String, Object> getParams(Map<String, Object> json) {
        final Object paramsObj = json.get("params");
        if (paramsObj == null)
            return Map.of();
        if (paramsObj instanceof Map)
            //noinspection unchecked
            return (Map<String, Object>) paramsObj;
        return Map.of();
    }


    private String handleResult(Integer ignoredId, Map<String, Object> ignoredJson) {
        return null;
    }

    private static final Log log = LogFactory.getLog(Broker.class);
}
