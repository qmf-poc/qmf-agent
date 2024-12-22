package s4y.solutions.mp.components.args;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgumentDefinitionTest {
    @Test
    public void IntDefinition_shouldPass() {
        // Arrange
        String[] cli = {"-p", "8080"};
        IErrorHandler errorHandler = Mockito.mock(IErrorHandler.class);
        // Act
        ArgumentInt definition = ArgumentInt.define(errorHandler, params -> {
            params.cli(cli);
            params.shortArg("p");
            params.longArg("http_port");
            params.env("HTTP_PORT");
            params.property("http.port");
            params.defaultValue(0);
        });
        // Assert
        assertArrayEquals(cli, definition.cli);
        assertEquals("p", definition.shortArg);
        assertEquals("http_port", definition.longArg);
        assertEquals("HTTP_PORT", definition.env);
        assertEquals("http.port", definition.property);
        assertEquals(0, definition.defaultValue);
        assertEquals(errorHandler, definition.errorHandler);
    }

    @Test
    public void StringDefinition_shouldPass() {
        // Arrange
        String[] cli = {"-p", "8080"};
        // Act
        ArgumentString definition = ArgumentString.define(params -> {
            params.cli(cli);
            params.shortArg("p");
            params.longArg("http_port");
            params.env("HTTP_PORT");
            params.property("http.port");
            params.defaultValue("0");
        });
        // Assert
        assertArrayEquals(cli, definition.cli);
        assertEquals("p", definition.shortArg);
        assertEquals("http_port", definition.longArg);
        assertEquals("HTTP_PORT", definition.env);
        assertEquals("http.port", definition.property);
        assertEquals("0", definition.defaultValue);
    }
}
