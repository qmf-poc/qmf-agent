package s4y.solutions.mp.components.args;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class ArgumentUseTest {
    @SetEnvironmentVariable(key = "DB2_USER", value = "admin")
    @Test
    public void stringArgument_shouldBePickedUpFromEnv() {
        // Arrange
        ArgumentString definition = ArgumentString.define(params -> {
            params.cli(new String[]{});
            params.shortArg("u");
            params.longArg("db2_user");
            params.env("DB2_USER");
            params.property("db2.user");
            params.defaultValue("anonymous");
        });
        // Act
        String argument = definition.use();
        // Assert
        assertEquals("admin", argument);
    }

    @SetEnvironmentVariable(key = "HTTP_PORT", value = "8080")
    @Test
    public void intArgument_shouldBePickedUpFromEnv() {
        // Arrange
        ArgumentInt definition = ArgumentInt.define(mock(IErrorHandler.class),params -> {
            params.cli(new String[]{});
            params.shortArg("p");
            params.longArg("http_port");
            params.env("HTTP_PORT");
            params.property("http.port");
            params.defaultValue(0);
        });
        // Act
        int argument = definition.use();
        // Assert
        assertEquals(8080, argument);
    }

    @ParameterizedTest(name = "{index} => {0}")
    @FieldSource("TEST_CASES")
    void testArgument(ArgumentTestCase<?> testCase) {
        for (int i = 0; i < testCase.properties.length - 1; i += 2) {
            System.setProperty(testCase.properties[i], testCase.properties[i + 1]);
        }
        if (testCase instanceof StringTestCase) {
            // Arrange
            StringTestCase stringTestCase = (StringTestCase) testCase;
            ArgumentString definition = ArgumentString.define(arg -> {
                arg.cli(stringTestCase.cli);
                if (stringTestCase.shortArg != null) arg.shortArg(stringTestCase.shortArg);
                if (stringTestCase.longArg != null) arg.longArg(stringTestCase.longArg);
                if (stringTestCase.env != null) arg.env(stringTestCase.env);
                if (stringTestCase.property != null) arg.property(stringTestCase.property);
                arg.defaultValue(stringTestCase.defaultValue);
            });
            // Act
            String argument = definition.use();
            // Assert
            assertEquals(stringTestCase.expected, argument);
        } else if (testCase instanceof IntTestCase) {
            // Arrange
            IntTestCase intTestCase = (IntTestCase) testCase;
            ArgumentInt definition = ArgumentInt.define(intTestCase.errorHandler,arg -> {
                arg.cli(intTestCase.cli);
                if (intTestCase.shortArg != null) arg.shortArg(intTestCase.shortArg);
                if (intTestCase.longArg != null) arg.longArg(intTestCase.longArg);
                if (intTestCase.env != null) arg.env(intTestCase.env);
                if (intTestCase.property != null) arg.property(intTestCase.property);
                arg.defaultValue(intTestCase.defaultValue);
            });
            // Act
            int argument = definition.use();
            // Assert
            assertEquals(intTestCase.expected, argument);
        } else {
            fail("Unknown test case type");
        }
    }

    static abstract class ArgumentTestCase<T> {
        final String description;
        final T expected;
        final String[] cli;
        final String[] environment;
        final String[] properties;
        final String shortArg;
        final String longArg;
        final String env;
        final String property;

        ArgumentTestCase(String description, T expected, String[] cli, String[] environment, String[] properties, String shortArg, String longArg, String env, String property) {
            this.description = description;
            this.expected = expected;
            this.cli = cli;
            this.environment = environment;
            this.properties = properties;
            this.shortArg = shortArg;
            this.longArg = longArg;
            this.env = env;
            this.property = property;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    static class StringTestCase extends ArgumentTestCase<String> {
        final String defaultValue;

        StringTestCase(String description, String expected, String[] cli, String[] environment, String[] properties, String shortArg, String longArg, String env, String property, String defaultValue) {
            super(description, expected, cli, environment, properties, shortArg, longArg, env, property);
            this.defaultValue = defaultValue;
        }
    }

    static class IntTestCase extends ArgumentTestCase<Integer> {
        final int defaultValue;
        final IErrorHandler errorHandler;

        IntTestCase(String description, Integer expected, String[] cli, String[] environment, String[] properties, String shortArg, String longArg, String env, String property, int defaultValue, IErrorHandler errorHandler) {
            super(description, expected, cli, environment, properties, shortArg, longArg, env, property);
            this.defaultValue = defaultValue;
            this.errorHandler = errorHandler;
        }
    }

    static final IErrorHandler errorHandler = mock(IErrorHandler.class);
    @SuppressWarnings("unused")
    static final ArgumentTestCase<?>[] TEST_CASES = {
            new StringTestCase(
                    "string by short arg",
                    "admin",
                    new String[]{"-u", "admin"},
                    new String[]{},
                    new String[]{},
                    "u", "db2_user", "DB2_USER", "db2.user", "anonymous"),
            new StringTestCase(
                    "string by long arg",
                    "admin",
                    new String[]{"--db2_user", "admin"},
                    new String[]{},
                    new String[]{},
                    "-", "db2_user", "DB2_USER", "db2.user", "anonymous"),
            new StringTestCase(
                    "string by property",
                    "admin",
                    new String[]{},
                    new String[]{},
                    new String[]{"db2.user", "admin"},
                    "-", "db2_user", null, "db2.user", "anonymous"),
            new IntTestCase(
                    "int by short arg",
                    8080,
                    new String[]{"-p", "8080"},
                    new String[]{},
                    new String[]{},
                    "p", "http_port", "HTTP_PORT", "http.port", 0, errorHandler),
            new IntTestCase(
                    "int by long arg",
                    8080,
                    new String[]{"--http_port", "8080"},
                    new String[]{},
                    new String[]{},
                    "p", "http_port", "HTTP_PORT", "http.port", 0, errorHandler),
            new IntTestCase(
                    "int by property",
                    8080,
                    new String[]{},
                    new String[]{},
                    new String[]{"http.port", "8080"},
                    "p", "http_port", null, "http.port", 0, errorHandler)
    };
}
