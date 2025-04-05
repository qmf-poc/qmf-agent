package qmf.poc.agent.run;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrunkHtmlTest {
    @Test
    public void testTrunkHtmlIfLess() throws IOException {
        // Arrange
        final String html = Files.readString(Paths.get("src/test/resources/sample.html"));
        // Act
        final String result = QMFObjectRunner.trunkHTMLTable(html,4);
        // Assert
        String expected = Files.readString(Paths.get("src/test/resources/expected3.html"));
        assertEquals(expected, result);
    }

    @Test
    public void testTrunkHtmlIfMore() throws IOException {
        // Arrange
        final String html = Files.readString(Paths.get("src/test/resources/sample3.html"));
        // Act
        final String result = QMFObjectRunner.trunkHTMLTable(html,6);
        // Assert
        String expected = Files.readString(Paths.get("src/test/resources/expected3.html"));
        assertEquals(expected, result);
    }

    @Test
    public void testTrunkHtmlIfMore1() throws IOException {
        // Arrange
        final String html = Files.readString(Paths.get("src/test/resources/sample3.html"));
        // Act
        final String result = QMFObjectRunner.trunkHTMLTable(html,5);
        // Assert
        String expected = Files.readString(Paths.get("src/test/resources/expected3.html"));
        assertEquals(expected, result);
    }

    @Test
    public void testTrunkHtmlIfEqual() throws IOException {
        // Arrange
        final String html = Files.readString(Paths.get("src/test/resources/sample3.html"));
        // Act
        final String result = QMFObjectRunner.trunkHTMLTable(html,4);
        // Assert
        String expected = Files.readString(Paths.get("src/test/resources/expected3.html"));
        assertEquals(expected, result);
    }

}
