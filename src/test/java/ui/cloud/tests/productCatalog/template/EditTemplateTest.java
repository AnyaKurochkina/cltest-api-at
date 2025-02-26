package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.template.TemplatePage;
import ui.elements.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.TagSteps.deleteTagByName;

@Feature("Редактирование шаблона")
public class EditTemplateTest extends TemplateBaseTest {

    private static final List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTags() {
        for (String name : tagList) {
            deleteTagByName(name);
        }
    }

    @Test
    @TmsLinks({@TmsLink("506509"), @TmsLink("972523")})
    @DisplayName("Редактирование шаблона")
    public void editTemplateTest() {
        template.setDescription("New description");
        template.setTitle("New title");
        new ControlPanelIndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .setAttributes(template)
                .saveWithoutPatchVersion();
        new TemplatePage().checkAttributes(template);
    }

    @Test
    @TmsLink("529569")
    @DisplayName("Проверка сохранения версии")
    public void saveWithManualVersionTest() {
        String currentVersion = "1.0.1";
        new ControlPanelIndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .setRunQueue("test_1")
                .saveWithPatchVersion()
                .setRunQueue("test_2")
                .checkSaveWithInvalidVersion("1.0.1", currentVersion)
                .checkSaveWithInvalidVersion("1.0.0", currentVersion)
                .checkSaveWithInvalidVersionFormat("1/0/2")
                .saveWithManualVersion("1.0.2")
                .checkTemplateVersion("1.0.2");
    }

    @Test
    @TmsLink("602641")
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        new ControlPanelIndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .checkTemplateVersion("1.0.0")
                .setRunQueue("test_1")
                .saveWithManualVersion("1.0.999")
                .checkTemplateVersion("1.0.999")
                .setRunQueue("test_2")
                .saveWithPatchVersion()
                .checkTemplateVersion("1.1.0")
                .setRunQueue("test_3")
                .saveWithManualVersion("1.999.999")
                .checkTemplateVersion("1.999.999")
                .setRunQueue("test_4")
                .saveWithPatchVersion()
                .checkTemplateVersion("2.0.0")
                .setRunQueue("test_5")
                .saveWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("602884")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        new ControlPanelIndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .checkTemplateVersion("1.0.0")
                .setRunQueue("test_1")
                .saveWithManualVersion("1.0.999")
                .checkTemplateVersion("1.0.999")
                .setRunQueue("test_2")
                .checkNextVersionAndSave("1.1.0")
                .checkTemplateVersion("1.1.0")
                .setRunQueue("test_3")
                .saveWithManualVersion("1.999.999")
                .checkTemplateVersion("1.999.999")
                .setRunQueue("test_4")
                .checkNextVersionAndSave("2.0.0")
                .checkTemplateVersion("2.0.0")
                .setRunQueue("test_5")
                .saveWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("1186452")
    @DisplayName("Баннер при возврате с формы с несохраненными данными")
    public void checkUnsavedChangesAlert() {
        new ControlPanelIndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .checkUnsavedChangesAlertAccept(template)
                .checkUnsavedChangesAlertDismiss();
    }

    @Test
    @TmsLink("SOUL-5941")
    @DisplayName("Добавить и удалить новый тег со страницы шаблона")
    public void addAndDeleteNewTagOnPage() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        tagList.add(name);
        TemplatePage page = new ControlPanelIndexPage().goToTemplatesPage().findAndOpenTemplatePage(template.getName());
        page.addNewTag(name);
        page.saveWithoutPatchVersion(page.getSaveTemplateAlertText());
        page.deleteTag(name);
        page.saveWithoutPatchVersion(page.getSaveTemplateAlertText());
        assertTrue(new Table("Наименование").isEmpty());
    }
}
