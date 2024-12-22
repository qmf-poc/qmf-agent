package s4y.solutions.mp.workflows;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.agent.Agent;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.http.IHttpServer;

import java.util.function.Supplier;

public class Workflow {
    public static <T> T a() {
        return null;
    }
    public static <S extends IHttpServer, D_S extends IHttpServer.Definition<S>,
            CPL extends IConnectionsPool, D_CPL extends IConnectionsPool.Definition<CPL>>

    WithAgent<S, D_S, CPL, D_CPL> setAgentDefinition(Supplier<Agent.Definition<S, D_S, CPL, D_CPL>> init) {
        logger.debug("Configuring the agent...");
        Agent.Definition<S, D_S, CPL, D_CPL> definition = init.get();
        logger.info("Agent configured");
        return new WithAgent<>(definition);
    }

    static final Logger logger = LoggerFactory.getLogger(Workflow.class);
}
