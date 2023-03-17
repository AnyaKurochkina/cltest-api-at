package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.GraphSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphExportTest extends Tests {

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
}
