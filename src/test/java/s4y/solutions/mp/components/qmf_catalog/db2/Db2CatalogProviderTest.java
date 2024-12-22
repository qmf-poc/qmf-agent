package s4y.solutions.mp.components.qmf_catalog.db2;

import org.junit.jupiter.api.Test;
import s4y.solutions.mp.annotation.SkipIfConnectionNotConfigured;
import s4y.solutions.mp.components.connections_pool.naive.NaiveConnectionsPool;
import s4y.solutions.mp.components.qmf_catalog.Catalog;
import s4y.solutions.mp.errorhandler.DefaultErrorHandler;
import s4y.solutions.mp.errorhandler.IErrorHandler;

public class Db2CatalogProviderTest {
    IErrorHandler errorHandler = new DefaultErrorHandler();
    Db2CatalogProvider.Definition<NaiveConnectionsPool, NaiveConnectionsPool.Definition> definition =
            Db2CatalogProvider.define(errorHandler, provider ->
                    provider.connectionPool(() ->
                            NaiveConnectionsPool.define(errorHandler, pool -> {
                                pool.db2cs(arg -> {
                                    arg.env("DB2_CONNECTION_STRING");
                                    arg.property("db2.connection.string");
                                });
                                pool.db2user(arg -> {
                                    arg.env("DB2_USER");
                                    arg.property("db2.user");
                                });
                                pool.db2password(arg -> {
                                    arg.env("DB2_PASSWORD");
                                    arg.property("db2.password");
                                });
                            })));

    @SkipIfConnectionNotConfigured
    @Test
    public void testGetCatalog() {
        // Arrange
        Db2CatalogProvider<NaiveConnectionsPool> db2CatalogProvider =
                definition.use();
        // Act
        Catalog catalog = db2CatalogProvider.getCatalog();
        // Assert
        assert catalog != null;
    }
}
