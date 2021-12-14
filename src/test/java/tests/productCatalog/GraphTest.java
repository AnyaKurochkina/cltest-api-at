package tests.productCatalog;

import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Action.createAction.response.CreateActionResponse;
import httpModels.productCatalog.Graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.Graphs.deleteGraph.response.DeleteGraphResponse;
import httpModels.productCatalog.Graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.Product.createProduct.response.CreateProductResponse;
import httpModels.productCatalog.Service.createService.response.CreateServiceResponse;
import models.productCatalog.Graph;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.productCatalog.ActionsSteps;
import steps.productCatalog.GraphSteps;
import steps.productCatalog.ProductsSteps;
import steps.productCatalog.ServiceSteps;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
public class GraphTest {

    GraphSteps graphSteps = new GraphSteps();
    ProductsSteps productsSteps = new ProductsSteps();
    ActionsSteps actionsSteps = new ActionsSteps();
    ServiceSteps serviceSteps = new ServiceSteps();

    Graph graph;

    @Order(1)
    @DisplayName("Создание графа")
    @Test
    public void createGraph() {
        graph = Graph.builder().name("at_test_graph_api").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка графа")
    @Test
    public void getGraphsList() {
        Assertions.assertTrue(graphSteps.getGraphsList().size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования графа по имени")
    @Test
    public void checkGraphExists() {
        Assertions.assertTrue(graphSteps.isExist(graph.getName()));
        Assertions.assertFalse(graphSteps.isExist("NoExistsAction"));
    }

//    @Order(4)
//    @DisplayName("Импорт графа")
//    @Test
//    public void importGraph() {
//        String data = new JsonHelper().getStringFromFile("/productCatalog/graphs/importGraph.json");
//        String graphName = new JsonPath(data).get("Graphs.json.name");
//        graphSteps.importGraph(Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json");
//        Assertions.assertTrue(graphSteps.isExist(graphName));
//        //   graphSteps.deleteActionByName(actionName);
//     //   Assertions.assertFalse(graphSteps.isExist(graphName));
//    }

    @Order(5)
    @DisplayName("Получение графа по Id")
    @Test
    public void getGraphById() {
        GetGraphResponse graphById = graphSteps.getGraphById(graph.getGraphId());
        Assertions.assertEquals(graph.getName(), graphById.getName());
    }

    @Order(6)
    @DisplayName("Копирование графа по Id")
    @Test
    public void copyActionById() {
        String cloneName = graph.getName() + "-clone";
        graphSteps.copyGraphById(graph.getGraphId());
        Assertions.assertTrue(graphSteps.isExist(cloneName));
        graphSteps.deleteGraphByName(cloneName);
        Assertions.assertTrue(graphSteps.isExist(cloneName));
    }

    @Order(7)
    @DisplayName("Частичное обновление графа по Id")
    @Test
    public void partialUpdateGraph() {
        String expectedDescription = "UpdateDescription";
        String oldGraphVersion = graph.getVersion();
        graphSteps.partialUpdateGraphById(graph.getGraphId(), new JSONObject().put("description", expectedDescription));
        GetGraphResponse getGraphResponse = graphSteps.getGraphById(graph.getGraphId());
        String actualDescription = getGraphResponse.getDescription();
        String newGraphVersion = getGraphResponse.getVersion();
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertNotEquals(oldGraphVersion, newGraphVersion);
    }

    @Order(12)
    @DisplayName("Негативный тест на создание графа с существующим именем")
    @Test
    public void createGraphWithSameName() {
        graphSteps.createGraphResponse(graphSteps.createJsonObject(graph.getName())).assertStatus(400);
    }

    @Order(13)
    @ParameterizedTest
    @DisplayName("Негативный тест на создание графа с недопустимыми символами в имени.")
    @MethodSource("dataName")
    public void createGraphWithInvalidCharacters(String name) {
        graphSteps.createGraphResponse(graphSteps.createJsonObject(name)).assertStatus(400);
    }

    private static Stream<Arguments> dataName() {
        return Stream.of(
                Arguments.of("NameWithUppercase"),
                Arguments.of("nameWithUppercaseInMiddle"),
                Arguments.of("имя"),
                Arguments.of("Имя"),
                Arguments.of("a&b&c")
        );
    }

    @Order(99)
    @Test
    @DisplayName("Попытка удаления графа используемого в продукте, действии и сервисе")
    public void deleteUsedGraph() {
        CreateGraphResponse mainGraph = graphSteps.createGraph(graphSteps.createJsonObject("mainGraph"));
        String mainGraphId = mainGraph.getId();
        graphSteps.createGraph(graphSteps.createJsonObject("secondGraph"));
        graphSteps.partialUpdateGraphById(mainGraphId, new JSONObject().put("description", "updateVersion2.0")
                .put("version", "2.0.0"));
        graphSteps.partialUpdateGraphById(mainGraphId, new JSONObject().put("description", "updateVersion3.0")
                .put("version", "3.0.0"));
        CreateProductResponse createProductResponse = productsSteps.createProduct(new JsonHelper().getJsonTemplate("productCatalog/products/createProduct.json")
                .set("name", "product_for_graph_test_api")
                .set("graph_id", mainGraphId)
                .build()).extractAs(CreateProductResponse.class);
        CreateServiceResponse createServiceResponse = serviceSteps.createService(new JsonHelper().getJsonTemplate("productCatalog/services/createServices.json")
                .set("name", "service_for_graph_test_api")
                .set("graph_id", mainGraphId)
                .build()).extractAs(CreateServiceResponse.class);
        CreateActionResponse createActionResponse = actionsSteps.createAction(new JsonHelper().getJsonTemplate("productCatalog/actions/createAction.json")
                .set("name", "action_for_graph_test_api")
                .set("graph_id", mainGraphId)
                .build()).extractAs(CreateActionResponse.class);
        String deleteResponse = graphSteps.deleteGraphResponse(mainGraphId)
                .assertStatus(400)
                .extractAs(DeleteGraphResponse.class)
                .getErr();
        productsSteps.deleteProductById(createProductResponse.getId());
        serviceSteps.deleteServiceById(createServiceResponse.getId());
        actionsSteps.deleteActionById(createActionResponse.getId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление графа")
    @MarkDelete
    public void deleteAction() {
        try (Graph graph = Graph.builder().name("at_test_graph_api").build().createObjectExclusiveAccess()) {
            graph.deleteObject();
        }
    }
}
