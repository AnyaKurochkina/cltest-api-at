package ui.cloud.tests.productCatalog.graph;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;

@Feature("Просмотр списка графов")
public class GraphsListTest extends GraphBaseTest {

    @Test
    @TmsLink("486416")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkGraphsListSorting() {
        new ControlPanelIndexPage().goToGraphsPage()
                .checkGraphsListHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate();
    }

    @Test
    @TmsLink("962859")
    @DisplayName("Поиск в списке графов")
    public void searchGraphTest() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findGraphByValue(NAME, graph)
                .findGraphByValue(TITLE, graph)
                .findGraphByValue(NAME.substring(1).toUpperCase(), graph)
                .findGraphByValue(TITLE.substring(1).toUpperCase(), graph);
    }

    @Test
    @TmsLink("807492")
    @DisplayName("Возврат в список со страницы графа")
    public void returnFromGraphPageTest() {
        new ControlPanelIndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .returnToGraphsList()
                .checkGraphIsHighlighted(NAME);
        new GraphsListPage().openGraphPage(NAME);
        Selenide.back();
        new GraphsListPage().checkGraphIsHighlighted(NAME);
    }
}
