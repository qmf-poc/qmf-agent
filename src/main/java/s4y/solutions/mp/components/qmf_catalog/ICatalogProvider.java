package s4y.solutions.mp.components.qmf_catalog;

/**
 * <p>ICatalogProvider interface.</p>
 *
 * @author dsa
 */
public interface ICatalogProvider extends AutoCloseable {
    /**
     * <p>getCatalog.</p>
     *
     * @return a {@link s4y.solutions.mp.components.qmf_catalog.Catalog} object
     */
    Catalog getCatalog();
    interface Definition<T extends ICatalogProvider> {
       T use();
    }
}
