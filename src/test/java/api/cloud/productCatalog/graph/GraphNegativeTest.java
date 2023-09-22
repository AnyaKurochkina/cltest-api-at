package api.cloud.productCatalog.graph;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphNegativeTest extends Tests {

    @DisplayName("Негативный тест на получение графа по Id без токена")
    @TmsLink("642636")
    @Test
    public void getGraphByIdWithOutTokenTest() {
        Graph graph = createGraph("graph_get_by_id_without_token_test_api");
        String message = getGraphByIdWithOutToken(graph.getGraphId()).jsonPath().get("error.message");
        assertEquals("Unauthorized", message);
    }

    @DisplayName("Негативный тест на копирование графа по Id без токена")
    @TmsLink("642645")
    @Test
    public void copyGraphByIdWithOutTokenTest() {
        Graph graph = createGraph("graph_clone_without_token_test_api");
        assertEquals("Unauthorized", copyGraphByIdWithOutToken(graph.getGraphId()));
    }

    @DisplayName("Негативный тест на обновление графа по Id без токена")
    @TmsLink("642662")
    @Test
    public void updateGraphByIdWithOutToken() {
        Graph graph = createGraph("update_graph_without_token_test_api");
        assertEquals("Unauthorized", partialUpdateGraphWithOutToken(graph.getGraphId(), new JSONObject()
                .put("description", "UpdateDescription")));
    }

    @DisplayName("Негативный тест на попытку обновления графа до текущей версии")
    @TmsLink("642668")
    @Test
    public void partialUpdateForCurrentVersion() {
        Graph graph = createGraph("partial_update_graph_test_api");
        String currentVersion = graph.getVersion();
        String message = partialUpdateGraph(graph.getGraphId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Версия %s для %s уже существует", currentVersion, graph.getName()), message);
    }

    @DisplayName("Негативный тест на создание графа с существующим именем")
    @TmsLink("642678")
    @Test
    public void createGraphWithSameName() {
        Graph graph = createGraph("create_graph_with_exist_name_test_api");
        String message = createGraph(graph.toJson()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"name\": graph с таким name уже существует.", message);
    }

    @DisplayName("Негативный тест на удаление графа без токена")
    @TmsLink("642695")
    @Test
    public void deleteGraphWithOutToken() {
        Graph graph = createGraph("delete_graph_without_token_test_api");
        String message = deleteGraphByIdWithOutToken(graph.getGraphId()).jsonPath().get("error.message");
        assertEquals("Unauthorized", message);
    }

    @DisplayName("Негативный тест на получение списка графа по несуществующему ID")
    @TmsLink("1044118")
    @Test
    public void getGraphsByNotExistId() {
        String message = getGraphListById("not-exist-id").extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"id\": Введите правильный UUID.", message);
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
        String error = createGraph(jsonObject).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(format("{\"modifications\": [{\"err_message\": [\"Field values (envs) non-unique: ({})\"], \"err_details\":" +
                        " {\"fields\": [\"envs\"], \"objects\": [{\"name\": \"json_schema_dev_mod\"," +
                        " \"envs\": [\"dev\"]}], \"entity\": \"GraphModification\", \"error_code\":" +
                        " \"VALUES_OF_LIST_ARE_NOT_UNIQUE\"}}]}",
                env.getValue()), error);
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
        String error = createGraph(jsonObject).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(format("{\"modifications\": [{\"err_message\": [\"Environment type is not in the directory\"]," +
                " \"err_details\": {\"fields\": [\"envs\"], \"objects\": [{\"envs\": [\"{}\"]}]," +
                " \"entity\": \"GraphModification\", \"error_code\": \"ENV_DOES_NOT_EXISTS\"}}]}", env.getValue()), error);
    }
}
