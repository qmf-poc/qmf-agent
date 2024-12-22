package s4y.solutions.mp.workflows;

import s4y.solutions.mp.agent.Agent;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.http.IHttpServer;

import java.util.function.Function;

public class CheckPreconditions<S extends IHttpServer, CPL extends IConnectionsPool> {
   private final Agent<S, CPL> agent;

    CheckPreconditions(Agent<S, CPL> agent){
        this.agent = agent;
    }

    public IfOk<S, CPL> checkPreconditions(Function<Agent<S, CPL>, Integer> check) {
        int result = check.apply(agent);
        return new IfOk<>(result, agent);
    }
}
