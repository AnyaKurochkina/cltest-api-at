package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphExportTest extends Tests {
    private static Graph simpleGraph;
    private static Graph simpleGraph2;

    @BeforeAll
    public static void setUp() {
        simpleGraph = createGraph("export_graph1_test_api");
        simpleGraph2 = createGraph("export_graph2_test_api");
    }

    @DisplayName("Экспорт графа по Id")
    @TmsLink("1507286")
    @Test
    public void exportGraphByIdTest() {
        String graphName = "graph_export_test_api";
        JSONObject jsonObject = Graph.builder()
                .name(graphName)
                .title(graphName)
                .build()
                .init()
                .toJson();
        Graph graph = createGraph(jsonObject).assertStatus(201).extractAs(Graph.class);
        String body = exportGraphById(graph.getGraphId()).toString();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/graphs/exportGraph.json";
        DataFileHelper.write(filePath, body);
        deleteGraphByName(graphName);
        importGraph(filePath);
        assertTrue(isGraphExists(graphName));
        deleteGraphByName(graphName);
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких графов")
    @TmsLink("1520078")
    @Test
    public void multiExportGraphsTest() {
        ExportEntity e = new ExportEntity(simpleGraph.getGraphId(), simpleGraph.getVersion());
        ExportEntity e2 = new ExportEntity(simpleGraph2.getGraphId(), simpleGraph2.getVersion());
        exportObjectsById("graphs", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте графа")
    @TmsLink("SOUL-")
    @Test
    public void checkExportedObjectsFieldGraphTest() {
        String graphName = "graph_exported_objects_test_api";
        Graph graph = createGraph(graphName);
        Response response = exportGraphById(graph.getGraphId());
        LinkedHashMap r = response.jsonPath().get("exported_objects.Graph.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(graph.getLastVersion(), jsonObject.get("last_version_str").toString());
        assertEquals(graph.getName(), jsonObject.get("name").toString());
        assertEquals(graph.getVersion(), jsonObject.get("version").toString());
    }
}
