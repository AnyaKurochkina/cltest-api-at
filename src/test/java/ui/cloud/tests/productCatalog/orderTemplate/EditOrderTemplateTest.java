package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.orderTemplate.OrderTemplatePage;
import ui.elements.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;

public class EditOrderTemplateTest extends OrderTemplateBaseTest {

    private static final List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTags() {
        for (String name : tagList) {
            deleteTagByName(name);
        }
    }

    @Test
    @TmsLink("1073593")
    @DisplayName("Баннер при возврате с формы с несохраненными данными")
    public void checkUnsavedChangesAlert() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .checkUnsavedChangesAlertAccept(orderTemplate)
                .checkUnsavedChangesAlertDismiss();
    }

    @Test
    @TmsLink("SOUL-1977")
    @DisplayName("Добавить и удалить существующий тег со страницы шаблона")
    public void addAndDeleteTagOnPage() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        tagList.add(name);
        OrderTemplatePage page = new ControlPanelIndexPage().goToOrderTemplatesPage().findAndOpenTemplatePage(orderTemplate.getName());
        page.addExistingTag(name);
        page.saveWithoutPatchVersion(page.getSaveTemplateAlertText());
        page.deleteTag(name);
        page.saveWithoutPatchVersion(page.getSaveTemplateAlertText());
        assertTrue(new Table("Наименование").isEmpty());
    }
}
