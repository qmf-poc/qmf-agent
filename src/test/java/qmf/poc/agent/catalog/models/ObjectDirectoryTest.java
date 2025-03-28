package qmf.poc.agent.catalog.models;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ObjectDirectoryTest {
    @Test
    public void testDeserialize() {
        // Arrange
        final Gson gson = new Gson();
        final String serializedObjectDirectory = "{\"owner\":\"owner\",\"name\":\"name\",\"type\":\"type\",\"subType\":\"subtype\",\"objectLevel\":1,\"restricted\":\"\",\"model\":\"\",\"created\":\"\",\"modified\":\"\",\"lastUsed\":\".\"}";
        // Act
        final ObjectDirectory objectDirectory = gson.fromJson(serializedObjectDirectory, ObjectDirectory.class);
        // Assert
        assertNotNull(objectDirectory);
        assertEquals("owner", objectDirectory.owner);
        assertEquals("name", objectDirectory.name);
        assertEquals("type", objectDirectory.type);
        assertEquals("subtype", objectDirectory.subType);
        assertEquals(1, objectDirectory.objectLevel);
        assertEquals("", objectDirectory.restricted);
        assertEquals("", objectDirectory.model);
        assertEquals("", objectDirectory.created);
        assertEquals("", objectDirectory.modified);
        assertEquals(".", objectDirectory.lastUsed);
    }
}
