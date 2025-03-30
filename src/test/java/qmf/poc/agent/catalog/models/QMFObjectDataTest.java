package qmf.poc.agent.catalog.models;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QMFObjectDataTest {
    @Test
    public void testDeserialize() {
        // Arrange
        final Gson gson = new Gson();
        final String serializedObjectData = "{\"owner\":\"owner\",\"name\":\"name\",\"type\":\"type\",\"appldata\":\"appldata\"}";
        // Act
        final ObjectData objectData = gson.fromJson(serializedObjectData, ObjectData.class);
        // Assert
        assertNotNull(objectData);
        assertEquals("owner", objectData.owner);
        assertEquals("name", objectData.name);
        assertEquals("type", objectData.type);
        assertEquals("appldata", objectData.appldata);
    }
}
