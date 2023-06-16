package core.helper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import core.helper.Page;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Objects;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@EqualsAndHashCode
public class Response {
    final int status;
    String responseMessage;
    @Getter
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
            throw new StatusResponseException(s, status(), http.method, Objects.isNull(http.role) ? "None" : http.role.toString(), http.token, headers, http.host + http.path, http.body, responseMessage);
        return this;
    }

    public int status() {
        return status;
    }

    public JSONObject toJson() {
        return new JSONObject(toString());
    }

    public JsonPath jsonPath() {
        return response.jsonPath();
    }

    public String getContentType() {
        String contType = response.getContentType();
        int start = contType.indexOf("/");
        int end = 0;
        if (contType.contains(";")) {
            end = contType.indexOf(";");
        } else {
            end = contType.length();
        }
        return contType.substring(start + 1, end);
    }

    public Response compareWithJsonSchema(String path) {
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(path));
        return this;
    }

    @SneakyThrows
    public <T> T extractAs(Class<T> clazz) {
        JSONObject jsonObject = new JSONObject(responseMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(jsonObject.toMap(), clazz);
    }

    @SneakyThrows
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public <T extends Page<?>> T extractAllPages(Class<T> clazz) {
        int i = 1;
        T page = extractAs(clazz);
        List items = page.getList();
        int count = items.size();
        if(Objects.nonNull(page.getMeta()))
            count = page.getMeta().getTotalCount();
        while (count > items.size()) {
            http.path = http.path.replaceAll("page=(\\d+)", "page=" + (++i));
            page = http.setSourceToken("").filterRequest().assertStatus(200).extractAs(clazz);
            items.addAll(page.getList());
        }
        page.setList(items);
        Assertions.assertEquals(count, items.size(), "Размер списка не равен TotalCount");
        return page;
    }

    public String toString() {
        return responseMessage;
    }

}
