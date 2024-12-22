package s4y.solutions.mp.workflows;

import s4y.solutions.mp.agent.Agent;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.http.IHttpServer;

import java.util.function.Function;

public class IfOk<S extends IHttpServer, CPL extends IConnectionsPool> {
    private final int result;
    private final Agent<S, CPL> agent;

    IfOk(int result, Agent<S, CPL> agent) {
        this.result = result;
        this.agent = agent;
    }

    public WaitForCtrlC<S, CPL> ifOk(Function<Agent<S, CPL>, Integer> ifOk) {
        if (result == 0)
            ifOk.apply(agent);
        return new WaitForCtrlC<>(result, agent);
    }
}
