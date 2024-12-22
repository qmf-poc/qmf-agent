package s4y.solutions.mp.components.connections_pool;

import java.sql.Connection;

/**
 * <p>IConnectionsPool interface.</p>
 *
 * @author dsa
 */
public interface IConnectionsPool extends AutoCloseable{
    /**
     * <p>getConnection.</p>
     *
     * @return a {@link java.sql.Connection} object
     */
    Connection getConnection();
    /**
     * <p>testConnectionString.</p>
     *
     * @return a boolean
     */
    boolean testConnectionString();
    interface Definition<T extends IConnectionsPool> {
        T use();
    }
}
