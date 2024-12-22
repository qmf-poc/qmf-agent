package s4y.solutions.mp.components.http;


/**
 * <p>IHttpServer interface.</p>
 *
 * @author dsa
 */
public interface IHttpServer extends AutoCloseable {
    /**
     * <p>listen.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    Boolean start();
    void stop();
    interface Definition<T extends IHttpServer> {
        T use();
    }
}
