package ui.cloud.tests.productCatalog.jinja2Template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.jinja2Template.Jinja2TemplatePage;
import ui.elements.Alert;

import static core.helper.StringUtils.getClipBoardText;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("Редактирование шаблона Jinja2")
public class EditJinja2TemplateTest extends Jinja2TemplateBaseTest {

    @Test
    @TmsLink("713212")
    @DisplayName("Редактирование шаблона Jinja2")
    public void editForbiddenAction() {
        jinja2Template.setDescription("New description");
        jinja2Template.setJinja2Template("{%- set result = '' -%}\n" +
                "{%- set result = user_info+'\\n'+' project_info' -%}    \n" +
                "{{ result }}");
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage()
                .findAndOpenJinja2TemplatePage(NAME)
                .setAttributes(jinja2Template);
        Jinja2TemplatePage page = new Jinja2TemplatePage();
        page.getSaveButton().click();
        Alert.green("Шаблон отредактирован");
        page.checkAttributes(jinja2Template);
    }

    @Test
    @TmsLink("1607686")
    @DisplayName("Баннер при несохраненных изменениях")
    public void checkUnsavedChangesAlert() {
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage()
                .findAndOpenJinja2TemplatePage(NAME)
                .checkUnsavedChangesAlertAccept(jinja2Template)
                .checkUnsavedChangesAlertDismiss();
    }

    @Test
    @TmsLink("713313")
    @DisplayName("Форматирование шаблона Jinja2, копирование в буфер обмена")
    public void formatTemplate() {
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage()
                .findAndOpenJinja2TemplatePage(NAME);
        jinja2Template.setJinja2Template("{ %   -    set    full_policy_name = \n" +
                "('kv-' + project_name + '-' + policy_name) -%}\n" +
                "{{ full_policy_name }}");
        Jinja2TemplatePage page = new Jinja2TemplatePage();
        page.setAttributes(jinja2Template);
        page.getFormatButton().click();
        Alert.green("Текст отформатирован");
        jinja2Template.setJinja2Template("{% - set full_policy_name = ('kv-' + project_name + '-' + policy_name) -%}\n" +
                "{{ full_policy_name }}");
        assertEquals(jinja2Template.getJinja2Template(), page.getTemplateTextArea().getValue());
        page.getCopyToClipboardButton().click();
        Alert.green("Данные успешно скопированы");
        assertEquals("{% - set full_policy_name = ('kv-' + project_name + '-' +" +
                " policy_name) -%}{{ full_policy_name }}", getClipBoardText());
    }
}
