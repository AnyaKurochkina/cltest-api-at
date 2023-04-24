package ui.cloud.tests.productCatalog.jinja2Template;

import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.jinja2Template.Jinja2TemplatePage;
import ui.elements.Alert;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.Jinja2Steps.deleteJinjaByName;
import static ui.elements.TypifiedElement.scrollCenter;

@Feature("Создание шаблона Jinja2")
public class CreateJinja2TemplateTest extends Jinja2TemplateBaseTest {

    private final String result = "[\"kv-proj-k0x1pshcki-portal-ro\",\"kv-proj-k0x1pshcki-user-ro\"]";

    @Test
    @TmsLink("710122")
    @DisplayName("Создание шаблона Jinja2")
    public void createJinja2TemplateTest() {
        checkNameValidation();
        createWithoutRequiredParameters();
        createWithNonUniqueName();
        createJinja2Template();
    }

    @Step("Создание шаблона Jinja2 без заполнения обязательных полей")
    private void createWithoutRequiredParameters() {
        jinja2Template.setName(NAME + "_");
        new ControlPanelIndexPage().goToJinja2TemplatesListPage()
                .addNewJinja2Template()
                .checkRequiredParams(jinja2Template);
    }

    @Step("Создание шаблона Jinja2 с неуникальным кодом")
    private void createWithNonUniqueName() {
        jinja2Template.setName(NAME);
        new ControlPanelIndexPage().goToJinja2TemplatesListPage()
                .addNewJinja2Template()
                .checkNonUniqueNameValidation(jinja2Template);
    }

    @Step("Создание шаблона Jinja2 с недопустимым кодом")
    private void checkNameValidation() {
        new ControlPanelIndexPage().goToJinja2TemplatesListPage()
                .addNewJinja2Template()
                .checkNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }

    @Step("Создание шаблона Jinja2")
    private void createJinja2Template() {
        jinja2Template.setName(NAME + "_");
        new ControlPanelIndexPage().goToJinja2TemplatesListPage()
                .addNewJinja2Template()
                .setAttributes(jinja2Template);
        Jinja2TemplatePage page = new Jinja2TemplatePage();
        page.getTestTemplateButton().getButton().scrollIntoView(true).click();
        Waiting.find(() -> page.getResultTextArea().getWhitespacesRemovedValue().equals(result), Duration.ofSeconds(2));
        page.getClearResultButton().getButton().scrollIntoView(scrollCenter).click();
        assertTrue(page.getResultTextArea().getValue().isEmpty());
        page.getCreateButton().click();
        Alert.green("Шаблон успешно создан");
        page.checkAttributes(jinja2Template);
        deleteJinjaByName(jinja2Template.getName());
    }
}
