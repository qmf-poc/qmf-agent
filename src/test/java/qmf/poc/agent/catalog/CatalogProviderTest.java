package qmf.poc.agent.catalog;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import qmf.poc.agent.Args;
import qmf.poc.agent.catalog.models.Catalog;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CatalogProviderTest {
    @Test
    @Disabled("Integration")
    public void testCatalog() throws ParseException, SQLException {
        // Arrange
        try (final CatalogProvider provider = new CatalogProvider(new Args(new String[0]))) {
            // Act
            final Catalog catalog = provider.catalog();
            // Assert
            assertNotNull(catalog);
            assertNotEquals(0, catalog.qmfObjects.size());
            /*
            assertNotEquals(0, catalog.objectData.size());
            assertNotEquals(0, catalog.objectDirectories.size());
            assertNotEquals(0, catalog.objectRemarks.size());
             */
        }
    }
}
