package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

@Epic("Графы")
@Feature("Просмотр списка графов")
public class GraphsListTest extends GraphBaseTest {

    @Test
    @TmsLink("486416")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkGraphsListSorting() {
        new IndexPage().goToGraphsPage()
                .checkGraphsListHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate();
    }

    @Test
    @TmsLink("962859")
    @DisplayName("Поиск в списке графов")
    public void searchGraph() {
        new IndexPage().goToGraphsPage()
                .findGraphByName(NAME)
                .findGraphByName(TITLE)
                .findGraphByName(NAME.substring(1).toUpperCase())
                .findGraphByTitle(TITLE.substring(1).toUpperCase());
    }
}
