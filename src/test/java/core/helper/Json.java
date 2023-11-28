package core.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.restassured.path.json.JsonPath;
import org.json.JSONObject;

import java.io.IOException;

@JsonDeserialize(using = Json.JsonDeserializer.class)
public class Json {
    private final String json;

    public Json(String json) {
        this.json = json;
    }

    public JsonPath jsonPath(){
        return JsonPath.from(json);
    }

    public JSONObject get(){
        return new JSONObject(json);
    }

    @Override
    public String toString() {
        return json;
    }

    public static class JsonDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<Json> {
        @Override
        public Json deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            return new Json(node.toString());
        }
    }
}
