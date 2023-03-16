package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.graph.Graph;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ProductSteps.importProduct;
@Tag("product_catalog")
@Tag("Graphs")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphImportTest extends Tests {

    private static final String PATHNAME = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json";


    @DisplayName("Импорт графа")
    @TmsLink("642628")
    @Test
    public void importGraphTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        String graphName = new JsonPath(data).get("Graph.name");
        if (isGraphExists(graphName)) {
            deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        }
        importGraph(PATHNAME).assertStatus(200);
        assertTrue(isGraphExists(graphName));
        deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        assertFalse(isGraphExists(graphName));
    }

    @DisplayName("Импорт нескольких графов")
    @TmsLink("1507294")
    @Test
    public void importMultiGraphTest() {
        String graphName = "import_graph_test_api";
        if (isGraphExists(graphName)) {
            deleteGraphByName(graphName);
        }
        String graphName2 = "import_graph2_test_api";
        if (isGraphExists(graphName2)) {
            deleteGraphByName(graphName2);
        }
        Graph graph1 = createGraph(graphName);
        Graph graph2 = createGraph(graphName2);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/multiImportGraph.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/multiImportGraph2.json";
        DataFileHelper.write(filePath, exportGraphById(graph1.getGraphId()).toString());
        DataFileHelper.write(filePath2, exportGraphById(graph2.getGraphId()).toString());
        deleteGraphByName(graphName);
        deleteGraphByName(graphName2);
        importObjects("graphs", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isGraphExists(graphName), "Граф не существует");
        assertTrue(isGraphExists(graphName2), "Граф не существует");
        deleteGraphByName(graphName);
        deleteGraphByName(graphName2);
    }

    @DisplayName("Импорт уже существующего графа")
    @TmsLink("1320916")
    @Test
    public void importExistGraphTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        String graphName = new JsonPath(data).get("Graph.name");
        if (isGraphExists(graphName)) {
            deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        }
        importGraph(PATHNAME).assertStatus(200);
        String expectedMsg = "Error loading dump: (Graph: import_graph_test_api, version = 1.0.0), ['Версия \"1.0.0\" Graph:import_graph_test_api уже существует. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.']";
        String message = importGraph(PATHNAME).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, message);
        assertTrue(isGraphExists(graphName), "Граф не существует");
        deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        assertFalse(isGraphExists(graphName), "Граф существует");
    }

    @Test
    @DisplayName("Негативный тест импорт графа в другой раздел")
    @TmsLink("1320923")
    public void importGraphToAnotherSection() {
        String expectedMsg = "Импортируемый объект \"Graph\" не соответствует разделу \"Product\"";
        String message = importProduct(PATHNAME).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, message);
    }
}
