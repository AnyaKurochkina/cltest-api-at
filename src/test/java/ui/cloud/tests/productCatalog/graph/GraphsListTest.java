package ui.cloud.tests.productCatalog.graph;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;
import ui.uiModels.Graph;

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
    public void searchGraphTest() {
        new IndexPage().goToGraphsPage()
                .findGraphByName(NAME)
                .findGraphByName(TITLE)
                .findGraphByName(NAME.substring(1).toUpperCase())
                .findGraphByTitle(TITLE.substring(1).toUpperCase());
    }

    @Test
    @TmsLink("807492")
    @DisplayName("Возврат в список со страницы графа")
    public void returnFromGraphPageTest() {
        new IndexPage().goToGraphsPage()
                .sortByCreateDate()
                .lastPage()
                .openGraphPage(NAME)
                .returnToGraphsList()
                .checkGraphIsHighlighted(NAME);
        new GraphsListPage().openGraphPage(NAME);
        Selenide.back();
        new GraphsListPage().checkGraphIsHighlighted(NAME);
    }

    @Test
    @TmsLink("807492")
    @DisplayName("Возврат в список со страницы графа")
    public void openGraph() {
        new IndexPage().goToGraphsPage()
                .sortByCreateDate()
                .lastPage()
                .openGraphPage(NAME)
                .returnToGraphsList()
                .checkGraphIsHighlighted(NAME);
        new GraphsListPage().openGraphPage(NAME);
        Selenide.back();
        new GraphsListPage().checkGraphIsHighlighted(NAME);
    }

    @Test
    @DisplayName("Открытие графа в новой вкладке с переключением")
    public void openGraphInNewTabAndSwitchTest() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphInNewTab(new Graph(NAME));
    }
}
