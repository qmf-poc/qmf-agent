package qmf.poc.agent.ws;

import java.util.Optional;

public interface WebSocketConnection extends AutoCloseable {
    Optional<Throwable> listen();
}
