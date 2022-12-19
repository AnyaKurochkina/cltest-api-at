package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.createGraph;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class GraphNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("graphs/",
            "productCatalog/graphs/createGraph.json");

    @DisplayName("Негативный тест на получение графа по Id без токена")
    @TmsLink("642636")
    @Test
    public void getGraphByIdWithOutToken() {
        Graph graph = Graph.builder()
                .name("graph_get_by_id_without_token_test_api")
                .build()
                .createObject();
        steps.getByIdWithOutToken(graph.getGraphId());
    }

    @DisplayName("Негативный тест на копирование графа по Id без токена")
    @TmsLink("642645")
    @Test
    public void copyGraphByIdWithOutToken() {
        Graph graph = Graph.builder()
                .name("graph_clone_without_token_test_api")
                .build()
                .createObject();
        steps.copyByIdWithOutToken(graph.getGraphId());
    }

    @DisplayName("Негативный тест на обновление графа по Id без токена")
    @TmsLink("642662")
    @Test
    public void updateGraphByIdWithOutToken() {
        Graph graph = Graph.builder()
                .name("update_graph_without_token_test_api")
                .version("1.0.0")
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(graph.getGraphId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на попытку обновления графа до текущей версии")
    @TmsLink("642668")
    @Test
    public void partialUpdateForCurrentVersion() {
        Graph graph = Graph.builder()
                .name("partial_update_graph_test_api")
                .version("1.0.0")
                .build()
                .createObject();
        String currentVersion = graph.getVersion();
        steps.partialUpdateObject(graph.getGraphId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @DisplayName("Негативный тест на создание графа с существующим именем")
    @TmsLink("642678")
    @Test
    public void createGraphWithSameName() {
        Graph graph = Graph.builder()
                .name("create_graph_with_exist_name_test_api")
                .version("1.0.0")
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(graph.getName())).assertStatus(400);
    }

    @DisplayName("Негативный тест на удаление графа без токена")
    @TmsLink("642695")
    @Test
    public void deleteGraphWithOutToken() {
        Graph graph = Graph.builder()
                .name("delete_graph_without_token_test_api")
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(graph.getGraphId());
    }

    @DisplayName("Негативный тест на получение списка графа по несуществующему ID")
    @TmsLink("1044118")
    @Test
    public void getGraphsByNotExistId() {
        Response response = steps.getResponseGraphListById("not-exist-id");
        assertEquals("Введите правильный UUID.", response.jsonPath().getList("id").get(0));
    }

    @DisplayName("Негативный тест на создание графа с неуникальным значением поля envs в модификациях")
    @TmsLink("1353676")
    @Test
    public void createGraphsWithNotUniqueEnvsFieldInMod() {
        Env env = Env.DEV;
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Arrays.asList(env, env, env))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data("dev_title")
                .build();
        JSONObject jsonObject = Graph.builder()
                .name("create_graph_with_not_unique_envs_field")
                .version("1.0.0")
                .modifications(Collections.singletonList(jsonSchema))
                .build()
                .init()
                .toJson();
        String error = createGraph(jsonObject).assertStatus(400).jsonPath()
                .getList("modifications[0].non_field_errors", String.class).get(0);
        assertEquals(String.format("Env field values are not unique: %s", env.getValue()), error);
    }

    @DisplayName("Негативный тест на создание графа с не валидным значением поля envs в модификациях")
    @TmsLink("1353678")
    @Test
    public void createGraphsWithInvalidEnvsFieldInMod() {
        Env env = Env.SOME_VALUE;
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Collections.singletonList(env))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data("dev_title")
                .build();
        JSONObject jsonObject = Graph.builder()
                .name("create_graph_with_invalid_envs_field_in_mod_test_api")
                .version("1.0.0")
                .modifications(Collections.singletonList(jsonSchema))
                .build()
                .init()
                .toJson();
        String error = createGraph(jsonObject).assertStatus(400).jsonPath()
                .getList("modifications[0].non_field_errors", String.class).get(0);
        assertEquals(String.format("Env field values are not valid: %s", env.getValue()), error);
    }
}
