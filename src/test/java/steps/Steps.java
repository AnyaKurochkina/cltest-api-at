package steps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import models.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class Steps {
    public static final String dataFolder = Configure.getAppProp("data.folder");
    public static final String folder_logs = Configure.getAppProp("folder.logs");
    public static final String dataJson = Configure.getAppProp("data.json");
    public static final String titleInformationSystem = Configure.getAppProp("title_information_system");

    private static final int perPage = 100;

    @JsonIgnore
    @SneakyThrows
    protected static List<?> listEntities(String host, String path, Class<?> clazz, Role role) {
        return listEntities(host, path, clazz,"data", role);
    }

    protected static List<?> listEntities(String host, String path, Class<?> clazz, String pathData, Role role) {
        ObjectMapper objectMapper = JsonHelper.getCustomObjectMapper();
        JavaType typeList = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        List<? extends Entity> entityList = new ArrayList<>();
        int totalCount;
        int page = 1;
        do {
            JsonPath jsonPath = responseList(host, path, page++, role);
            totalCount = jsonPath.getInt("meta.total_count");
            entityList.addAll(objectMapper.convertValue(jsonPath.getList(pathData), typeList));
        }
        while (totalCount > entityList.size());
        return entityList;
    }

    private static JsonPath responseList(String host, String path, int page, Role role) {
        return new Http(host)
                .setRole(role)
                .get(path + "&include=members,total_count&page={}&per_page={}", page, perPage)
                .assertStatus(200)
                .jsonPath();
    }
}
