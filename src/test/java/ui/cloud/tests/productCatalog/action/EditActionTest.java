package ui.cloud.tests.productCatalog.action;

import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.actions.ActionPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.elements.Alert;
import ui.elements.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;

public class EditActionTest extends ActionBaseTest {

    private static final List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTags() {
        for (String name : tagList) {
            deleteTagByName(name);
        }
    }

    @Test
    @TmsLink("SOUL-6051")
    @DisplayName("Регистрация действия")
    public void registerAction() {
        String name = UUID.randomUUID().toString();
        createActionByApi(name);
        new ControlPanelIndexPage()
                .goToActionsListPage()
                .openActionPage(name)
                .getRegisterButton()
                .click();
        Alert.green("Действие успешно зарегистрировано");
        new ActionsListPage()
                .openActionPage(name)
                .getRegisterButton()
                .click();
        Alert.red("Ошибка при добавлении действия заказа");
    }

    @Test
    @TmsLink("SOUL-6068")
    @DisplayName("Добавить и удалить существующий тег со страницы действия")
    public void addAndDeleteTagOnPage() {
        String actionName = UUID.randomUUID().toString();
        createActionByApi(actionName);
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        tagList.add(name);
        ActionPage page = new ControlPanelIndexPage().goToActionsListPage().openActionPage(actionName);
        page.addExistingTag(name);
        page.saveWithoutPatchVersion(page.getSaveActionAlertText());
        page.deleteTag(name);
        page.saveWithoutPatchVersion(page.getSaveActionAlertText());
        assertTrue(new Table("Наименование").isEmpty());
    }
}
