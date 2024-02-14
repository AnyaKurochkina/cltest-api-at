package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.elements.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;
import static ui.cloud.pages.productCatalog.graph.GraphPage.SAVE_GRAPH_ALERT_TEXT;

@Feature("Редактирование графа")
public class EditGraphTest extends GraphBaseTest {

    private static final List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTags() {
        for (String name : tagList) {
            deleteTagByName(name);
        }
    }

    @Test
    @TmsLink("487709")
    @DisplayName("Сохранение графа с указанием версии вручную")
    public void saveGraphWithManualVersion() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .setAuthor("QA-1")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999");
    }

    @Test
    @TmsLink("529313")
    @DisplayName("Сохранение графа с указанием некорректной версии")
    public void saveGraphWithIncorrectVersion() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .setAuthor("QA-1")
                .saveGraphWithPatchVersion()
                .checkGraphVersion("1.0.1")
                .setAuthor("QA-2")
                .trySaveGraphWithIncorrectVersion("1.0.0", "1.0.1")
                .setAuthor("QA-3")
                .trySaveGraphWithIncorrectVersion("1.0.1", "1.0.1");
    }

    @Test
    @TmsLinks({@TmsLink("487621"), @TmsLink("600394")})
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .setAuthor("QA-1")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999")
                .setAuthor("QA-2")
                .saveGraphWithPatchVersion()
                .checkGraphVersion("1.1.0")
                .setAuthor("QA-3")
                .saveGraphWithManualVersion("1.999.999")
                .checkGraphVersion("1.999.999")
                .setAuthor("QA-4")
                .saveGraphWithPatchVersion()
                .checkGraphVersion("2.0.0")
                .setAuthor("QA-5")
                .saveGraphWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("600752")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .setAuthor("QA-1")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999")
                .setAuthor("QA-2")
                .checkNextVersionAndSave("1.1.0")
                .checkGraphVersion("1.1.0")
                .setAuthor("QA-3")
                .saveGraphWithManualVersion("1.999.999")
                .checkGraphVersion("1.999.999")
                .setAuthor("QA-4")
                .checkNextVersionAndSave("2.0.0")
                .checkGraphVersion("2.0.0")
                .setAuthor("QA-5")
                .saveGraphWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("SOUL-836")
    @DisplayName("Добавить и удалить существующий тег со страницы графа")
    public void addAndDeleteTagOnPage() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        tagList.add(name);
        GraphPage page = new ControlPanelIndexPage().goToGraphsPage().findAndOpenGraphPage(graph.getName());
        page.addExistingTag(name);
        page.saveWithoutPatchVersion(SAVE_GRAPH_ALERT_TEXT);
        page.deleteTag(name);
        page.saveWithoutPatchVersion(SAVE_GRAPH_ALERT_TEXT);
        assertTrue(new Table("Наименование").isEmpty());
    }

    @Test
    @TmsLink("SOUL-7157")
    @DisplayName("Добавить и удалить новый тег со страницы графа")
    public void addAndDeleteNewTagOnPage() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        tagList.add(name);
        GraphPage page = new ControlPanelIndexPage().goToGraphsPage().findAndOpenGraphPage(graph.getName());
        page.addNewTag(name);
        page.saveWithoutPatchVersion(SAVE_GRAPH_ALERT_TEXT);
        page.deleteTag(name);
        page.saveWithoutPatchVersion(SAVE_GRAPH_ALERT_TEXT);
        assertTrue(new Table("Наименование").isEmpty());
    }

    @Test
    @TmsLink("SOUL-8355")
    @DisplayName("Поднятие версии графа")
    public void increaseGraphVersion() {
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .increaseVersionAndSave("1.0.1", SAVE_GRAPH_ALERT_TEXT);
        new GraphPage().checkGraphVersion("1.0.1");
    }
}
