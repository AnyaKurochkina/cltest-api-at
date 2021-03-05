package ru.vtb.test.api.helper.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.sql.ResultSet;

public class JsonUtil {
    private static ObjectMapper objectMapper;

    static  {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());
        objectMapper.registerModule(module);
    }

    public static String sql2String(ResultSet rs) throws JsonProcessingException {
        ObjectNode node = objectMapper.createObjectNode();
        JsonNode json = node.putPOJO("results", rs).get("results");
        return objectMapper.writeValueAsString(json);
    }

    public static JsonNode sql2JsonNode(ResultSet rs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.putPOJO("results", rs);
        return node.get("results");
    }

    public static JsonNode string2Json(String str) throws JsonProcessingException {
        JsonNode node = objectMapper.reader().readTree(str);
        return node;
    }

    public static String compareJson(JsonNode node1, JsonNode node2) {
        String result = JsonDiff.asJson(node1, node2).asText(null);
        return result;
    }

}
