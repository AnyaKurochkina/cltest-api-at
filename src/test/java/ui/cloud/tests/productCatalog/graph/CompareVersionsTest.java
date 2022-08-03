package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.uiModels.Graph;

@Epic("Графы")
@Feature("Сравнение версий графа")
public class CompareVersionsTest extends GraphBaseTest {

    @Test
    @TmsLink("503532")
    @DisplayName("Сравнение версий графа")
    public void compareVersionsTest() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .editGraph(new Graph(NAME, "test", "test"))
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
