package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DiffPage;
import ui.cloud.pages.productCatalog.graph.GraphPage;

@Feature("Сравнение версий графа")
public class CompareVersionsTest extends GraphBaseTest {

    @Test
    @TmsLink("503532")
    @DisplayName("Сравнение версий графа")
    public void compareVersionsTest() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .setAuthor("QA-1")
                .saveGraphWithPatchVersion()
                .goToVersionComparisonTab();
        new DiffPage().checkCurrentVersionInDiff("1.0.1")
                .compareWithVersion("1.0.0");
        new GraphPage().selectGraphVersion("1.0.0");
        new DiffPage().checkCurrentVersionInDiff("1.0.0")
                .compareWithVersion("1.0.0")
                .compareWithVersion("1.0.1");
    }
}
