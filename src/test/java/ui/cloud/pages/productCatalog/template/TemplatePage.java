package ui.cloud.pages.productCatalog.template;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.TextArea;
import ui.models.Template;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplatePage extends BasePage {
    private static final String saveTemplateAlertText = "Шаблон успешно изменен";
    private final SelenideElement templatesListLink = $x("//a[text() = 'Список шаблонов узлов']");
    private final SelenideElement templateVersion = $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final Input nameInput = Input.byLabel("Код шаблона");
    private final Input titleInput = Input.byLabel("Наименование");
    private final TextArea description = TextArea.byName("description");
    private final Input runQueueInput = Input.byLabel("Название очереди для старта задачи");
    private final Input rollbackQueueInput = Input.byLabel("Название очереди для отката");
    private final DropDown typeDropDown = DropDown.byLabel("Тип");
    private final SelenideElement saveButton = $x("//div[text()='Сохранить']/parent::button");
    private final SelenideElement cancelButton = $x("//div[text()='Отмена']/parent::button");
    private final SelenideElement paramsTab = $x("//span[text()='Параметры']//parent::button");

    public TemplatePage() {
        templatesListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка, что отображаемая версия шаблона равна '{version}'")
    public TemplatePage checkTemplateVersion(String version) {
        templateVersion.shouldHave(Condition.exactText(version));
        return this;
    }

    @Step("Проверка атрибутов шаблона '{template.name}'")
    public TemplatePage checkTemplateAttributes(Template template) {
        nameInput.getInput().shouldHave(Condition.exactValue(template.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        description.getTextArea().shouldHave(Condition.exactValue(template.getDescription()));
        runQueueInput.getInput().shouldHave(Condition.exactValue(template.getRunQueue()));
        rollbackQueueInput.getInput().shouldHave(Condition.exactValue(template.getRollbackQueue()));
        Assertions.assertTrue(typeDropDown.getValue().equals(template.getType()));
        checkTemplateVersion(template.getVersion());
        return this;
    }

    @Step("Редактирование атрибутов шаблона '{template.name}'")
    public TemplatePage setTemplateAttributes(Template template) {
        nameInput.setValue(template.getName());
        titleInput.setValue(template.getTitle());
        description.setValue(template.getDescription());
        runQueueInput.setValue(template.getRunQueue());
        rollbackQueueInput.setValue(template.getRollbackQueue());
        typeDropDown.select(template.getType());
        return this;
    }

    @Step("Редактирование атрибута runQueue")
    public TemplatePage setRunQueue(String runQueue) {
        runQueueInput.setValue(runQueue);
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
        TestUtils.scrollToTheTop();
        templatesListLink.click();
        return new TemplatesListPage();
    }

    @Step("Сохранение шаблона со следующей патч-версией")
    public TemplatePage saveWithPatchVersion() {
        super.saveWithPatchVersion(saveTemplateAlertText);
        return new TemplatePage();
    }

    @Step("Сохранение шаблона без патч-версии")
    public TemplatePage saveWithoutPatchVersion() {
        saveWithoutPatchVersion(saveTemplateAlertText);
        return new TemplatePage();
    }

    @Step("Сохранение шаблона с указанием версии")
    public TemplatePage saveWithManualVersion(String newVersion) {
        super.saveWithManualVersion(newVersion, saveTemplateAlertText);
        return new TemplatePage();
    }

    @Step("Проверка сохранения шаблона с некорректно указанной версией '{newVersion}'")
    public TemplatePage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        super.checkSaveWithInvalidVersion(newVersion, currentVersion);
        return new TemplatePage();
    }

    @Step("Проверка сохранения шаблона с указанной версией некорректного формата '{newVersion}'")
    public TemplatePage checkSaveWithInvalidVersionFormat(String newVersion) {
        super.checkSaveWithInvalidVersionFormat(newVersion);
        return new TemplatePage();
    }

    @Step("Проверка недоступности сохранения шаблона при достижении лимита версий")
    public TemplatePage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']").shouldBe(Condition.visible);
        saveButton.shouldBe(Condition.disabled);
        return new TemplatePage();
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public TemplatePage checkNextVersionAndSave(String nextVersion) {
        super.checkNextVersionAndSave(nextVersion, saveTemplateAlertText);
        return this;
    }

    @Step("Назад в браузере и отмена в баннере о несохранённых изменениях")
    public TemplatePage backInBrowserAndDismissAlert() {
        back();
        dismissBrowserAlert();
        return this;
    }

    @Step("Возврат в список шаблонов и отмена в баннере о несохранённых изменениях")
    public TemplatePage goToTemplatesListAndDismissAlert() {
        templatesListLink.scrollIntoView(false).click();
        dismissBrowserAlert();
        return this;
    }

    @Step("Нажатие кнопки Назад и отмена в баннере о несохранённых изменениях")
    public TemplatePage backAndDismissAlert() {
        backButton.click();
        dismissBrowserAlert();
        return this;
    }

    @Step("Отмена в баннере о несохранённых изменениях")
    public void dismissBrowserAlert() {
        assertEquals("Внесенные изменения не сохранятся. Покинуть страницу?", switchTo().alert().getText());
        dismiss();
        TestUtils.wait(500);
    }

    @Step("Переход на вкладку 'Параметры'")
    public TemplatePage goToParamsTab() {
        paramsTab.click();
        TestUtils.wait(500);
        return new TemplatePage();
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public TemplatePage goToVersionComparisonTab() {
        TestUtils.scrollToTheTop();
        versionComparisonTab.click();
        return this;
    }
}
