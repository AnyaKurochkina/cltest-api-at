package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.models.Template;

import java.util.UUID;

@Epic("Шаблоны узлов")
@Feature("Редактирование шаблона")
public class EditTemplateTest extends TemplateBaseTest {

    @Test
    @TmsLinks({@TmsLink("506509"), @TmsLink("972523")})
    @DisplayName("Редактирование шаблона")
    public void editTemplateTest() {
        Template template = new Template(UUID.randomUUID().toString());
        Template template2 = new Template(UUID.randomUUID().toString());
        template2.setTitle("Edited title");
        template2.setDescription("Edited description");
        new IndexPage().goToTemplatesPage()
                .createTemplate(template)
                .goToTemplatesList()
                .findAndOpenTemplatePage(template.getName())
                .setTemplateAttributes(template2)
                .saveWithPatchVersion()
                .checkTemplateAttributes(template2)
                .goToTemplatesList()
                .deleteTemplate(template2.getName());
    }

    @Test
    @TmsLink("529569")
    @DisplayName("Проверка сохранения версии")
    public void saveWithManualVersionTest() {
        String currentVersion = "1.0.1";
        new IndexPage().goToTemplatesPage()
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
        new IndexPage().goToTemplatesPage()
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
        new IndexPage().goToTemplatesPage()
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
    @DisplayName("Баннер при возврате с формы с несохраненными данными (Отмена)")
    public void checkUnsavedChangesAlertAndCancel() {
        new IndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .setRunQueue("test1")
                .backAndDismissAlert()
                .goToTemplatesListAndDismissAlert()
                .cancelAndDismissAlert();
    }
}
