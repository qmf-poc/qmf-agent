package qmf.poc.agent.broker;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import qmf.poc.agent.Args;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.jsonrpc.JsonRpcHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonRpcHandlerTest {
    @Test
    public void testPing() {
        // Arrange
        final JsonRpcHandler jsonRpcHandler = new JsonRpcHandler(null, null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"params\":{\"payload\": \"pl\"},\"id\":1}";
        // Act
        final String pong = jsonRpcHandler.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":\"pong: pl\",\"id\":1,\"jsonrpc\":\"2.0\"}", pong);
    }

    @Test
    public void testPingEmptyParams() {
        // Arrange
        final JsonRpcHandler jsonRpcHandler = new JsonRpcHandler(null, null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"params\":{},\"id\":1}";
        // Act
        final String pong = jsonRpcHandler.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":\"pong: null\",\"id\":1,\"jsonrpc\":\"2.0\"}", pong);
    }

    @Test
    public void testPingNoParams() {
        // Arrange
        final JsonRpcHandler jsonRpcHandler = new JsonRpcHandler(null, null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"id\":1}";
        // Act
        final String pong = jsonRpcHandler.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals( "{\"result\":\"pong: null\",\"id\":1,\"jsonrpc\":\"2.0\"}", pong);
    }

    @Test
    public void testCatalog() throws ParseException {
        // Arrange
        final JsonRpcHandler jsonRpcHandler = new JsonRpcHandler(new CatalogProvider(new Args(new String[0])), null);
        final String pingSnapshot = "{\"jsonrpc\":\"2.0\",\"method\":\"snapshot\",\"id\":1}";
        // Act
        final String catalog = jsonRpcHandler.handleJsonRPC(pingSnapshot);
        // Assert
        final String beg = "{\"result\":{\"catalog\":{\"qmfObjects\":[";
        assertEquals(beg, catalog.substring(0, beg.length()));
    }
}
