package qmf.poc.agent.broker;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import qmf.poc.agent.Args;
import qmf.poc.agent.catalog.CatalogProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrokerTest {
    @Test
    public void testPing() {
        // Arrange
        final Broker broker = new Broker(null, null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"params\":{\"payload\": \"pl\"},\"id\":1}";
        // Act
        final String pong = broker.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":\"pong: pl\",\"id\":1,\"jsonrpc\":\"2.0\"}", pong);
    }

    @Test
    public void testPingEmptyParams() {
        // Arrange
        final Broker broker = new Broker(null, null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"params\":{},\"id\":1}";
        // Act
        final String pong = broker.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":\"pong: null\",\"id\":1,\"jsonrpc\":\"2.0\"}", pong);
    }

    @Test
    public void testPingNoParams() {
        // Arrange
        final Broker broker = new Broker(null, null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"id\":1}";
        // Act
        final String pong = broker.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals( "{\"result\":\"pong: null\",\"id\":1,\"jsonrpc\":\"2.0\"}", pong);
    }

    @Test
    public void testCatalog() throws ParseException {
        // Arrange
        final Broker broker = new Broker(new CatalogProvider(new Args(new String[0])), null);
        final String pingSnapshot = "{\"jsonrpc\":\"2.0\",\"method\":\"snapshot\",\"id\":1}";
        // Act
        final String catalog = broker.handleJsonRPC(pingSnapshot);
        // Assert
        final String beg = "{\"result\":{\"catalog\":{\"objectData\"";
        assertEquals(beg, catalog.substring(0, beg.length()));
    }
}
