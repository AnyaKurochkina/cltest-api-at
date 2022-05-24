package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class ViewJSONTest extends GraphBaseTest {

    @Test
    @DisplayName("Просмотр JSON графа")
    public void viewJSON() {
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .viewJSON();
    }
}
