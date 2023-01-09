package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.Configure;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductSteps.importProduct;
@Tag("Graphs")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GraphImportTest extends Tests {

    private static final String PATHNAME = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json";


    @DisplayName("Импорт графа")
    @TmsLink("642628")
    @Test
    public void importGraphTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        String graphName = new JsonPath(data).get("Graph.name");
        if (isGraphExists(graphName)) {
            deleteGraphById(getGraphByName(graphName).getGraphId());
        }
        importGraph(PATHNAME).assertStatus(200);
        assertTrue(isGraphExists(graphName));
        deleteGraphById(getGraphByName(graphName).getGraphId());
        assertFalse(isGraphExists(graphName));
    }

    @DisplayName("Импорт уже существующего графа")
    @TmsLink("1320916")
    @Test
    public void importExistGraphTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        String graphName = new JsonPath(data).get("Graph.name");
        if (isGraphExists(graphName)) {
            deleteGraphById(getGraphByName(graphName).getGraphId());
        }
        importGraph(PATHNAME).assertStatus(200);
        String expectedMsg = "Версия \"1.0.0\" Graph:import_graph_test_api уже существует. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.";
        String message = importGraph(PATHNAME).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, message);
        assertTrue(isGraphExists(graphName), "Граф не существует");
        deleteGraphById(getGraphByName(graphName).getGraphId());
        assertFalse(isGraphExists(graphName), "Граф существует");
    }

    @Test
    @DisplayName("Негативный тест импорт графа в другой раздел")
    @TmsLink("1320923")
    public void importGraphToAnotherSection() {
        String expectedMsg = "['Импортируемый объект \"Graph\" не соответствует разделу \"Product\"']";
        String message = importProduct(PATHNAME).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, message);
    }
}
