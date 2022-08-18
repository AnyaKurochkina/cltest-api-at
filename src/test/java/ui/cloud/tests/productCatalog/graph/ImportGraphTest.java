package ui.cloud.tests.productCatalog.graph;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.uiModels.Graph;

@Epic("Графы")
@Feature("Импорт графа")
@DisabledIfEnv("prod")
public class ImportGraphTest extends BaseTest {

    @Test
    @DisplayName("Импорт графа до первого существующего объекта")
    @TmsLink("636442")
    public void importGraphTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        JsonPath json = new JsonPath(data);
        String name = json.get("Graph.name");
        String title = json.get("Graph.title");
        new IndexPage()
                .goToGraphsPage()
                .importGraph("src/test/resources/json/productCatalog/graphs/importGraph.json")
                .findAndOpenGraphPage(name)
                .checkGraphAttributes(new Graph(name, title, "1.0.0"))
                .deleteGraph();
    }
}
