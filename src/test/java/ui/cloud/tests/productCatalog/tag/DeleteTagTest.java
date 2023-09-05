package ui.cloud.tests.productCatalog.tag;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.tag.TagPage;
import ui.cloud.pages.productCatalog.tag.TagsListPage;
import ui.elements.Alert;
import ui.elements.Dialog;

import java.util.Collections;

import static steps.productCatalog.GraphSteps.addTagListToGraph;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.TagSteps.createTag;

@Feature("Удаление тега")
public class DeleteTagTest extends TagTest {

    @Test
    @TmsLink("SOUL-1061")
    @DisplayName("Удалить неиспользуемый тег из списка")
    public void deleteTag() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        TagsListPage page = new ControlPanelIndexPage().goToTagsPage();
        page.search(name);
        page.openDeleteDialog(name);
        new Dialog("Удаление тега").clickButton("Да");
        Alert.green("Удаление выполнено успешно");
        page.checkTagNotFound(name);
    }

    @Test
    @TmsLink("SOUL-6690")
    @DisplayName("Удалить неиспользуемый тег со страницы")
    public void deleteTagOnPage() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        TagsListPage page = new ControlPanelIndexPage().goToTagsPage();
        page.search(name);
        page.openTagPage(name);
        new TagPage().getDeleteButton().click();
        new Dialog("Удаление тега").clickButton("Да");
        Alert.green("Удаление выполнено успешно");
        page.checkTagNotFound(name);
    }

    @Test
    @TmsLink("SOUL-1064")
    @DisplayName("Удалить используемый тег")
    public void deleteUsedTag() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        tagList.add(name);
        Graph graph = createGraph("qa_at_graph_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase());
        addTagListToGraph(Collections.singletonList(name), graph.getName());
        TagsListPage page = new ControlPanelIndexPage().goToTagsPage();
        page.search(name);
        page.openDeleteDialog(name);
        new Dialog("Удаление тега").clickButton("Да");
        Alert.red("Нельзя удалить тег {}. Он используется в {'Graph'", name);
        page.openTagPage(name);
        new TagPage().getDeleteButton().click();
        new Dialog("Удаление тега").clickButton("Да");
        Alert.red("Нельзя удалить тег {}. Он используется в {'Graph'", name);
    }
}
