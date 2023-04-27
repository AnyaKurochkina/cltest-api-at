package ui.cloud.tests.productCatalog.graph;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.GraphSteps.isGraphExists;

@Feature("Импорт графа")
public class ImportGraphTest extends GraphBaseTest {

    @Test
    @DisplayName("Импорт графа до первого существующего объекта")
    @TmsLink("636442")
    public void importGraphTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        JsonPath json = new JsonPath(data);
        String name = json.getString("Graph.name");
        if (isGraphExists(name)) deleteGraphByApi(name);
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .importGraph("src/test/resources/json/productCatalog/graphs/importGraph.json")
                .findAndOpenGraphPage(name)
                .checkAttributes(Graph.builder().name(name)
                        .title(json.getString("Graph.title"))
                        .version("1.0.0")
                        .description(json.getString("Graph.description"))
                        .author(json.getString("Graph.author"))
                        .build());
        deleteGraphByApi(name);
    }
}
