package core.helper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class Response {
    final int status;
    String responseMessage;
    final List<String> headers;
    Http http;

    public Response(int status, String responseMessage, List<String> headers, Http http) {
        this.status = status;
        this.http = http;
        this.responseMessage = responseMessage;
        if(Objects.isNull(responseMessage))
            this.responseMessage = "";
        this.headers = headers;
    }

    public Response assertStatus(int s) {
        if (s != status())
            throw new Http.StatusResponseException(String.format("\nexpected:<%d>\nbut was:<%d>\nMethod: %s\nToken: %s\nHeaders: \n%s\nRequest: %s\n%s\nResponse: %s\n", s, status(), http.method, http.token, String.join("\n", headers), http.host + http.path, http.body, responseMessage));
        return this;
    }

    public int status() {
        return status;
    }

    public JSONObject toJson() {
        try {
            return new JSONObject(toString());
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public JsonPath jsonPath() {
        try {
            return new JsonPath(toString());
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    @SneakyThrows
    public <T> T extractAs(Class<T> clazz){
        JSONObject jsonObject = new JSONObject(responseMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(jsonObject.toMap(), clazz);
    }

    public String toString() {
        return responseMessage;
    }

}
