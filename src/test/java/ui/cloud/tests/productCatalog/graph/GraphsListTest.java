package ui.cloud.tests.productCatalog.graph;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;

import java.util.Collections;

import static steps.productCatalog.GraphSteps.partialUpdateGraph;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;

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
                .checkGraphFoundByValue(NAME, graph)
                .checkGraphFoundByValue(TITLE, graph)
                .checkGraphFoundByValue(NAME.substring(1).toUpperCase(), graph)
                .checkGraphFoundByValue(TITLE.substring(1).toUpperCase(), graph);
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

    @Test
    @TmsLink("Test")
    @DisplayName("Частичное и полное совпадение тегов")
    public void searchGraphsByTags() {
        String tag1 = "qa_at_tag_1";
        String tag2 = "qa_at_tag_2";
        partialUpdateGraph(graph.getGraphId(), new JSONObject()
                .put("tag_list", Collections.singletonList(tag1)));
        createTag(tag2);
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .setTagsFilter(tag1, tag2)
                .applyFilters();
        GraphsListPage page = new GraphsListPage();
        page.checkGraphFoundByValue(graph.getName(), graph)
                .getTagsMatchingType()
                .select("Полное");
        page.applyFilters();
        page.checkGraphNotFound(graph.getName());
        deleteTagByName(tag2);
    }
}
