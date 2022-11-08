package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.models.Graph;

@Feature("Сравнение версий графа")
public class CompareVersionsTest extends GraphBaseTest {

    @Test
    @TmsLink("503532")
    @DisplayName("Сравнение версий графа")
    public void compareVersionsTest() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.0.0", "", "QA-1"))
                .saveGraphWithPatchVersion()
                .goToVersionComparisonTab()
                .checkCurrentVersionInDiff("1.0.1")
                .compareWithVersion("1.0.0")
                .selectGraphVersion("1.0.0")
                .goToVersionComparisonTab()
                .checkCurrentVersionInDiff("1.0.0")
                .compareWithVersion("1.0.0")
                .compareWithVersion("1.0.1");
    }
}
