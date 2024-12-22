package s4y.solutions.mp.workflows;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.agent.Agent;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.http.IHttpServer;

public class WithAgent <S extends IHttpServer, D_S extends IHttpServer.Definition<S>,
        CPL extends IConnectionsPool, D_CPL extends IConnectionsPool.Definition<CPL>> {
    private final  Agent.Definition<S, D_S, CPL, D_CPL> definition;

    WithAgent(@NotNull Agent.Definition<S, D_S, CPL, D_CPL> definition){
        this.definition = definition;
    }

    public CheckPreconditions<S, CPL> withAgent() {
        logger.debug("Instantiating the agent ...");
        Agent<S, CPL> agent = definition.use();
        logger.info("Agent instantiated");
        return new CheckPreconditions<>(agent);
    }

    static final Logger logger = LoggerFactory.getLogger(WithAgent.class);
}
