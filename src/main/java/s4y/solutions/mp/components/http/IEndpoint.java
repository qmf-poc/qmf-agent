package s4y.solutions.mp.components.http;

// TODO: a wrapper around path/handler intended to be used to generate OpenAPI
/**
 * <p>IEndpoint interface.</p>
 *
 * @author dsa
 */
public interface IEndpoint<H> {
    /**
     * <p>path.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String path();
    /**
     * <p>handler.</p>
     *
     * @return a H object
     */
    H handler();
}
