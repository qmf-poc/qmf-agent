package qmf.poc.agent.catalog.models;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CatalogTest {
    @Test
    public void testDeserialize() {
        // Arrange
        final Gson gson = new Gson();
        final String serializedCatalog = "{\"qmfObjects\":[{\"owner\":\"owner\",\"name\":\"name\",\"type\":\"type\",\"appldata\":\"appldata\", \"objectLevel\":1,\"restricted\":\"\",\"model\":\"\",\"created\":\"\",\"modified\":\"\",\"lastUsed\":\".\"}]}";
        // Act
        final Catalog catalog = gson.fromJson(serializedCatalog, Catalog.class);
        // Assert
        assertNotNull(catalog);
        assertEquals(1, catalog.qmfObjects.size());
        /*
        assertEquals(1, catalog.objectData.size());
        assertEquals(0, catalog.objectDirectories.size());
        assertEquals(0, catalog.objectRemarks.size());
         */
        final QMFObject objectData = catalog.qmfObjects.get(0);
        assertEquals("owner", objectData.owner);
        assertEquals("name", objectData.name);
        assertEquals("type", objectData.type);
        assertEquals("appldata", objectData.appldata);
    }
}
