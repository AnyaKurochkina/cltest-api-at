package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import httpModels.productCatalog.Graphs.getGraph.response.GetGraphResponse;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;
import steps.productCatalog.GraphSteps;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
public class GraphTest {

    GraphSteps graphSteps = new GraphSteps();

    @Order(1)
    @DisplayName("Создание графа")
    @Test
    public void createGraph() {
        graphSteps.createGraph("AtTestGraph");
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
        Assertions.assertTrue(graphSteps.isExist("AtTestGraph"));
        Assertions.assertFalse(graphSteps.isExist("NoExistsAction"));
    }

    @Order(4)
    @DisplayName("Импорт графа")
    @Test
    public void importGraph() {
        String data = new JsonHelper().getStringFromFile("/productCatalog/actions/importGraph.json");
        String graphName = new JsonPath(data).get("Action.json.name");
        graphSteps.importGraph(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importGraph.json");
        Assertions.assertTrue(graphSteps.isExist(graphName));
        //   graphSteps.deleteActionByName(actionName);
        Assertions.assertFalse(graphSteps.isExist(graphName));
    }

    @Order(5)
    @DisplayName("Получение графа по Id")
    @Test
    public void getGraphById() {
        GetGraphResponse graphById = graphSteps.getGraphById("7adbcad7-6184-4efc-9c63-d0ea7e0a551b");
        Assertions.assertEquals("AtTestGraph", graphById.getName());
    }

    @Order(6)
    @DisplayName("Копирование графа по Id")
    @Test
    public void copyActionById() {
        String cloneName = "AtTestGraph-clone";
        graphSteps.copyGraphById("7adbcad7-6184-4efc-9c63-d0ea7e0a551b");
        Assertions.assertTrue(graphSteps.isExist(cloneName));
    }

    @Order(7)
    @DisplayName("Частичное обновление графа по Id")
    @Test
    public void partialUpdateGraph() {
        String expectedValue = "UpdateDescription";
        graphSteps.partialUpdateGraphById("38926af8-c30a-46c0-bb0c-907852dd6ed0", "description", expectedValue);
        String actual = graphSteps.getGraphById("38926af8-c30a-46c0-bb0c-907852dd6ed0").getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }
}
