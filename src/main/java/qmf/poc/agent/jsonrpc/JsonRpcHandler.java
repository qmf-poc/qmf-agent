package qmf.poc.agent.jsonrpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.catalog.models.Catalog;
import qmf.poc.agent.run.QMFObjectRunner;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import static java.lang.Math.min;

public class JsonRpcHandler {
    private final CatalogProvider catalogProvider;
    private final QMFObjectRunner qmfObjectRunner;
    private final JsonRpcEncoder jsonRpcEncoder = new JsonRpcEncoder();

    public JsonRpcHandler(CatalogProvider catalogProvider, QMFObjectRunner qmfObjectRunner) {
        this.catalogProvider = catalogProvider;
        this.qmfObjectRunner = qmfObjectRunner;
    }

    private String handlePing(Long id, Map<String, Object> params) {
        String payload = params.get("payload") == null ? "null" : params.get("payload").toString();
        log.debug("handleMethod: ping, id=" + id + ", payload=" + payload);
        String result = "pong: " + payload;
        log.debug("handled: ping, id=" + id + ", result=\"" + result + "\"");
        return jsonRpcEncoder.formatResult(id, result);
    }

    private String handleSnapshot(Long id) throws SQLException {
        log.debug("method: snapshot, id=" + id);
        // TODO: handle exception join does not return?
        // final Catalog catalog = catalogProvider.catalogParallel().join();
        final Catalog catalog = catalogProvider.catalog();
        if (log.isDebugEnabled()) {
            String res = catalog.toString();
            log.debug("handled: snapshot, id=" + id + ", result=\"" + res.substring(0, min(200, res.length())) + "\"");
        }
        return jsonRpcEncoder.formatResult(id, Map.of("catalog", catalog));
    }

    private String handleRun(Long id, Map<String, Object> params) throws Exception {
        log.debug("method: run, id=" + id);

        String owner = (String) params.get("owner");
        if (owner == null) {
            throw new ParseException("Missing owner parameter", 0);
        }
        String name = (String) params.get("name");
        if (name == null) {
            throw new ParseException("Missing name parameter", 0);
        }

        Double nRowsS = (Double) params.get("limit");
        int nRows = nRowsS == null ? -1 : nRowsS.intValue();

        final String result = qmfObjectRunner.retrieveObjectHTML(owner, name, "html", nRows);

        if (log.isDebugEnabled()) {
            log.debug("handled: run, id=" + id + ", result=\"" + result.substring(0, min(200, result.length())) + "\"");
        }
        return jsonRpcEncoder.formatResult(id, Map.of("body", result, "owner", owner, "name", name));
    }

    public String handleJsonRPC(String message) {
        log.debug("handleJsonRPC: " + message);
        try {
            final Map<String, Object> json = jsonRpcEncoder.parse(message);
            final Double idd = (Double) json.get("id");
            final Long id = idd == null ? null : idd.longValue();

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
                return jsonRpcEncoder.formatError(id, 2, e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Failed to parse JSON-RPC message", e);
            return jsonRpcEncoder.formatError(null, 1, e.getMessage());
        }
    }

    private String handleMethod(Long id, Map<String, Object> json) throws Exception {
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


    private String handleResult(Long ignoredId, Map<String, Object> ignoredJson) {
        return null;
    }

    private static final Log log = LogFactory.getLog(JsonRpcHandler.class);
}
