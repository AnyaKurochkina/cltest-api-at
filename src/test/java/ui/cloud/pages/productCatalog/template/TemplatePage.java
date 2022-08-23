package ui.cloud.pages.productCatalog.template;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.uiModels.Template;

import static com.codeborne.selenide.Selenide.$x;

public class TemplatePage {
    private final SelenideElement templatesListLink = $x("//a[text() = 'Список шаблонов узлов']");
    private final SelenideElement templateVersion = $x("//div[@aria-labelledby='version']");
    private final SelenideElement deleteButton = $x("//span[text()='Удалить']/parent::button");
    private final Input nameInput = Input.byLabel("Код шаблона");
    private final Input titleInput = Input.byLabel("Наименование");
    private final SelenideElement descriptionInput = $x("//textarea[@name='description']");
    private final Input runQueueInput = Input.byLabel("Название очереди для старта задачи");
    private final Input rollbackQueueInput = Input.byLabel("Название очереди для отката");
    private final DropDown typeDropDown = DropDown.byLabel("Тип");

    public TemplatePage() {
        templatesListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка, что отображаемая версия шаблона равна '{version}'")
    public TemplatePage checkTemplateVersion(String version) {
        templateVersion.shouldBe(Condition.visible).shouldHave(Condition.exactText(version));
        return new TemplatePage();
    }

    @Step("Проверка атрибутов шаблона '{template.name}'")
    public TemplatePage checkTemplateAttributes(Template template) {
        nameInput.getInput().shouldHave(Condition.exactValue(template.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        descriptionInput.shouldHave(Condition.exactValue(template.getDescription()));
        runQueueInput.getInput().shouldHave(Condition.exactValue(template.getRunQueue()));
        rollbackQueueInput.getInput().shouldHave(Condition.exactValue(template.getRollbackQueue()));
        Assertions.assertTrue(typeDropDown.getValue().equals(template.getType()));
        checkTemplateVersion(template.getVersion());
        return this;
    }

    @Step("Открытие диалога удаления шаблона")
    public DeleteDialog openDeleteDialog() {
        deleteButton.click();
        return new DeleteDialog();
    }

    @Step("Удаление шаблона")
    public void deleteTemplate() {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDelete();
    }

    @Step("Возврат в список шаблонов")
    public TemplatesListPage goToTemplatesList() {
        templatesListLink.click();
        return new TemplatesListPage();
    }
}
