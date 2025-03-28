package qmf.poc.agent.broker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrokerTest {
    @Test
    public void testPing() {
        // Arrange
        final Broker broker = new Broker(null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"params\":{\"payload\": \"pl\"},\"id\":1}";
        // Act
        final String pong = broker.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":{\"payload\":\"pong: pl\"},\"id\":1,\"jsonrpc\":\"2.0\"}",pong);
    }
    @Test
    public void testPingEmptyParams() {
        // Arrange
        final Broker broker = new Broker(null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"params\":{},\"id\":1}";
        // Act
        final String pong = broker.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":{\"payload\":\"pong: null\"},\"id\":1,\"jsonrpc\":\"2.0\"}",pong);
    }
    @Test
    public void testPingNoParams() {
        // Arrange
        final Broker broker = new Broker(null);
        final String pingJsonRPC = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"id\":1}";
        // Act
        final String pong = broker.handleJsonRPC(pingJsonRPC);
        // Assert
        assertEquals("{\"result\":{\"payload\":\"pong: null\"},\"id\":1,\"jsonrpc\":\"2.0\"}",pong);
    }
}
