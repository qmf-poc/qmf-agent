package s4y.solutions.mp.components.http.sun;

import com.sun.net.httpserver.HttpHandler;
import s4y.solutions.mp.components.http.IEndpoint;

/**
 * <p>SunEndpoint class.</p>
 *
 * @author dsa
 */
public class SunEndpoint implements IEndpoint<HttpHandler> {
    private final String p;
    private final HttpHandler h;

    /**
     * <p>Constructor for SunEndpoint.</p>
     *
     * @param path a {@link java.lang.String} object
     * @param handler a {@link com.sun.net.httpserver.HttpHandler} object
     */
    public SunEndpoint(String path, HttpHandler handler) {
        this.p = path;
        this.h = handler;
    }

    /** {@inheritDoc} */
    @Override
    public HttpHandler handler() {
        return h;
    }

    /** {@inheritDoc} */
    @Override
    public String path() {
        return p;
    }
}
