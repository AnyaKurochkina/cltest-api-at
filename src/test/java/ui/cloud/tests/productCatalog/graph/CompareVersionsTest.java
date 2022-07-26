package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.uiModels.Graph;

@Epic("Графы")
@Feature("Сравнение версий графа")
public class CompareVersionsTest extends GraphBaseTest {

    @Test
    @DisplayName("Сравнение версий графа")
    public void compareVersionsTest() {
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .editGraph(new Graph("test", "test"))
                .saveGraphWithPatchVersion()
                .goToVersionComparisonTab()
                .checkCurrentVersion("1.0.1")
                .compareToVersion("1.0.0")
                .selectGraphVersion("1.0.0")
                .goToVersionComparisonTab()
                .checkCurrentVersion("1.0.0")
                .compareToVersion("1.0.0")
                .compareToVersion("1.0.1");
    }
}
