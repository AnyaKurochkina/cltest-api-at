package core.helper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.util.Objects;

public class Response {
    final int status;
    String responseMessage;
    io.restassured.response.Response response;
    Http http;

    public Response(io.restassured.response.Response response, Http http) {
        this.status = response.statusCode();
        this.http = http;
        this.responseMessage = response.getBody().asString();
        if (Objects.isNull(responseMessage))
            this.responseMessage = "";
        this.response = response;
    }

    public Response assertStatus(int s) {
        String headers = response.getHeaders().toString();
        if (s != status())
            throw new Http.StatusResponseException(String.format("\nexpected:<%d>\nbut was:<%d>\nMethod: %s\nToken: %s\nHeaders: \n%s\nRequest: %s\n%s\nResponse: %s\n", s, status(), http.method, http.token, headers, http.host + http.path, http.body, responseMessage), status);
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
            return response.jsonPath();
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    @SneakyThrows
    public <T> T extractAs(Class<T> clazz) {
        JSONObject jsonObject = new JSONObject(responseMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(jsonObject.toMap(), clazz);
    }

    public String toString() {
        return responseMessage;
    }

}
