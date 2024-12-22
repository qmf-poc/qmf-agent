package s4y.solutions.mp.workflows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.agent.Agent;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.http.IHttpServer;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class WaitForCtrlC<S extends IHttpServer, CPL extends IConnectionsPool> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final int result;
    private final Agent<S, CPL> agent;
    private volatile Consumer<Agent<S, CPL>> cleanup;

    public WaitForCtrlC(int result, Agent<S, CPL> agent) {
        this.result = result;
        this.agent = agent;
        if (result != 0) return;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Ctrl+C pressed");
            if (cleanup != null) cleanup.accept(agent);
            cleanup = null;
            latch.countDown();
        }));
    }

    public int waitForCtrlC(Consumer<Agent<S, CPL>> cleanup) {
        if (result != 0) {
            cleanup.accept(agent);
            return result;
        }

        logger.info("Waiting for Ctrl+C ...");
        this.cleanup = cleanup;
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Interrupted", e);
            Thread.currentThread().interrupt();
        }
        if (this.cleanup != null) this.cleanup.accept(agent);
        return result;
    }

    static final Logger logger = LoggerFactory.getLogger(WaitForCtrlC.class);
}
