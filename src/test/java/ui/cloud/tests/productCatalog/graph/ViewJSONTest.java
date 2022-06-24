package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

public class ViewJSONTest extends GraphBaseTest {

    @Test
    @TmsLink("489318")
    @DisplayName("Просмотр JSON графа")
    public void viewJSON() {
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .viewJSON();
    }
}
