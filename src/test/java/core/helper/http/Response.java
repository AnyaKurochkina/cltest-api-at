package core.helper.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.Page;
import io.restassured.path.json.JsonPath;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import models.AbstractEntity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Type;
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
    private <T> T extractValue(TypeReference<T> valueTypeRef) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseMessage, valueTypeRef);
    }

    public <T> T extractAs(TypeReference<T> valueTypeRef) {
        return extractValue(valueTypeRef);
    }

    public <T> T extractAs(Class<T> clazz) {
        return extractValue(new TypeReference<T>() {
            @Override
            public Type getType() {
                return clazz;
            }
        });
    }

    @SneakyThrows
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public <T extends Page<?>> T extractAllPages(Class<T> clazz) {
        int i = 1;
        T page = extractAs(clazz);
        List items = page.getList();
        int count = items.size();
        if (Objects.nonNull(page.getMeta()))
            count = page.getMeta().getTotalCount();
        while (count > items.size()) {
            http.queryParams.put("page", "" + (++i));
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
