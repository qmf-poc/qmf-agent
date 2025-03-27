import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class GSonTest {
    private final static Gson gson = new Gson();

    public static Map<String, Object> parseJson(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    @Test
    public void testDecodeJsonRPC() {
        String message = "{\"jsonrpc\":\"2.0\",\"method\":\"echo\",\"params\":[\"Hello, world!\"],\"id\":1}";
        Map<String, Object> map = parseJson(message);
        Assertions.assertEquals(List.of("Hello, world!"), map.get("params"));
        Assertions.assertEquals("2.0", map.get("jsonrpc"));
        Assertions.assertEquals(1.0f, map.get("id"));
    }
}
