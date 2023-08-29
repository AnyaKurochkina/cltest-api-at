package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductCatalogSteps.*;
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
        ImportObject importObject = importGraph(PATHNAME);
        assertEquals(graphName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isGraphExists(graphName));
        deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        assertFalse(isGraphExists(graphName));
    }

    @DisplayName("Импорт нескольких графов")
    @TmsLink("1507294")
    @Test
    public void importMultiGraphTest() {
        String graphName = "multi_import_graph_test_api";
        if (isGraphExists(graphName)) {
            deleteGraphByName(graphName);
        }
        String graphName2 = "multi_import_graph2_test_api";
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
        String graphName = "import_exist_graph_test_api";
        Graph graph = createGraph(graphName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/existGraphImport.json";
        DataFileHelper.write(filePath, exportGraphById(graph.getGraphId()).toString());
        importGraph(filePath);
        ImportObject importObject = importGraph(filePath);
        assertEquals("error", importObject.getStatus());
        assertEquals(String.format("Error loading dump: Версия \"%s\" %s:%s уже существует. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.",
                        graph.getVersion(), importObject.getModelName(), importObject.getObjectName()),
                importObject.getMessages().get(0));
        assertTrue(isGraphExists(graphName), "Граф не существует");
        deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        assertFalse(isGraphExists(graphName), "Граф существует");
    }

    @Test
    @DisplayName("Негативный тест импорт графа в другой раздел")
    @TmsLink("1320923")
    public void importGraphToAnotherSection() {
        String graphName = "import_graph_for_another_section_test_api";
        Graph graph = createGraph(graphName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraphAnother.json";
        DataFileHelper.write(filePath, exportGraphById(String.valueOf(graph.getGraphId())).toString());
        deleteGraphByName(graphName);
        importProduct(filePath);
        assertTrue(isGraphExists(graphName), "Граф не существует");
        deleteGraphByName(graphName);
        assertFalse(isGraphExists(graphName), "Граф существует");
    }

    @DisplayName("Импорт графа с tag_list")
    @TmsLink("SOUL-7104")
    @Test
    public void importGraphWithTagListTest() {
        String graphName = "graph_import_with_tag_list_test_api";
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraphWithTags.json";
        if (isGraphExists(graphName)) {
            deleteGraphByName(graphName);
        }
        List<String> expectedTagList = Arrays.asList("import_test", "test_import");
        JSONObject jsonObject = Graph.builder()
                .name(graphName)
                .tagList(expectedTagList)
                .build()
                .toJson();
        Graph graph = createGraph(jsonObject).extractAs(Graph.class);
        DataFileHelper.write(filePath, exportObjectByIdWithTags("graphs", graph.getGraphId()).toString());
        deleteGraphByName(graphName);
        importObjectWithTagList("graphs", filePath);
        assertEquals(expectedTagList, getGraphByName(graphName).getTagList());
        deleteGraphByName(graphName);
    }
}
