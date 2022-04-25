package tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

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
}
