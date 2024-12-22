package s4y.solutions.mp.components.http.sun.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.components.qmf_catalog.Catalog;
import s4y.solutions.mp.components.qmf_catalog.ICatalogProvider;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.io.OutputStream;

/**
 * <p>CatalogHandler class.</p>
 *
 * @author dsa
 */
public class CatalogHandler extends BaseHandler {
    private final ICatalogProvider catalogProvider;
    private final Gson gson = new Gson();

    /**
     * <p>Constructor for CatalogHandler.</p>
     *
     * @param catalogProvider a {@link s4y.solutions.mp.components.qmf_catalog.ICatalogProvider} object
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     */
    public CatalogHandler(ICatalogProvider catalogProvider, IErrorHandler errorHandler) {
        super(errorHandler);
        this.catalogProvider = catalogProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void handleSecure(HttpExchange exchange) throws Exception {
        logger.debug("Handling catalog request");
        Catalog catalog = catalogProvider.getCatalog();

        String jsonResponse = gson.toJson(catalog);


        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    static final Logger logger = LoggerFactory.getLogger(CatalogHandler.class.getName());
}
