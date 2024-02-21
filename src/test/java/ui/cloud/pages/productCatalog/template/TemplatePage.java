package ui.cloud.pages.productCatalog.template;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.template.Template;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.TextArea;

import java.time.Duration;
import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class TemplatePage extends EntityPage {
    private final String saveTemplateAlertText = "Шаблон успешно изменен";
    private final SelenideElement templatesListLink = $x("//a[text() = 'Список шаблонов узлов']");
    private final TextArea descriptionTextArea = TextArea.byName("description");
    private final Input runQueueInput = Input.byName("run");
    private final Input rollbackQueueInput = Input.byName("rollback");
    private final Input timeoutInput = Input.byName("timeout");
    private final TextArea input = TextArea.byLabel("Input");
    private final TextArea output = TextArea.byLabel("Output");
    private final TextArea printedOutput = TextArea.byLabel("Printed output");
    private final Select typeSelect = Select.byLabel("Тип");
    private final SelenideElement templateNameValidationHint =
            $x("//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement nonuniqueNameValidationHint =
            nameInput.getInput().$x("following::div[text()='Шаблон узлов с таким именем уже существует']");
    private final String requiredFieldText = "Поле обязательно для заполнения";
    private final SelenideElement nameRequiredFieldHint =
            nameInput.getInput().$x("ancestor::div[2]//div[text()='" + requiredFieldText + "']");
    private final SelenideElement titleRequiredFieldHint =
            titleInput.getInput().$x("ancestor::div[2]//div[text()='" + requiredFieldText + "']");
    private final SelenideElement runQueueRequiredFieldHint =
            runQueueInput.getInput().$x("ancestor::div[2]//div[text()='" + requiredFieldText + "']");

    public TemplatePage() {
        templatesListLink.shouldBe(Condition.visible);
    }

    @Step("Создание шаблона узлов '{template.name}'")
    public TemplatePage createTemplate(Template template) {
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionTextArea.setValue(template.getDescription());
        runQueueInput.setValue(template.getRun());
        rollbackQueueInput.setValue(template.getRollback());
        typeSelect.set(template.getType());
        timeoutInput.setValue(template.getTimeout());
        goToParamsTab();
        input.setValue(new JSONObject(template.getInput()).toString());
        output.setValue(new JSONObject(template.getOutput()).toString());
        printedOutput.setValue(new JSONArray((ArrayList) template.getPrintedOutput()).toString());
        saveButton.click();
        Alert.green("Шаблон успешно создан");
        Waiting.sleep(2000);
        return new TemplatePage();
    }

    @Step("Проверка, что отображаемая версия шаблона равна '{version}'")
    public TemplatePage checkTemplateVersion(String version) {
        Waiting.find(() -> versionSelect.getValue().equals(version), Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверка атрибутов шаблона '{template.name}'")
    public TemplatePage checkAttributes(Template template) {
        nameInput.getInput().shouldHave(Condition.exactValue(template.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        descriptionTextArea.getElement().shouldHave(Condition.exactValue(template.getDescription()));
        runQueueInput.getInput().shouldHave(Condition.exactValue(template.getRun()));
        rollbackQueueInput.getInput().shouldHave(Condition.exactValue(template.getRollback().toString()));
        assertEquals(typeSelect.getValue(), template.getType());
        timeoutInput.getInput().shouldHave(Condition.exactValue(String.valueOf(template.getTimeout())));
        checkTemplateVersion(template.getVersion());
        goToParamsTab();
        String printedOutputJSON = new JSONArray((ArrayList) template.getPrintedOutput()).toString();
        Assertions.assertEquals(printedOutputJSON, printedOutput.getWhitespacesRemovedValue());
        return this;
    }

    @Step("Редактирование атрибутов шаблона '{template.name}'")
    public TemplatePage setAttributes(Template template) {
        nameInput.setValue(template.getName());
        titleInput.setValue(template.getTitle());
        descriptionTextArea.setValue(template.getDescription());
        runQueueInput.setValue(template.getRun());
        rollbackQueueInput.setValue(template.getRollback());
        typeSelect.set(template.getType());
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
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
    }

    @Step("Возврат в список шаблонов")
    public TemplatesListPage goToTemplatesList() {
        templatesListLink.scrollIntoView(false).click();
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
        saveButton.getButton().shouldBe(Condition.disabled);
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
        goToTab("Параметры");
        return new TemplatePage();
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public TemplatePage goToVersionComparisonTab() {
        goToTab("Сравнение версий");
        return this;
    }

    @Step("Возврат в список шаблонов по кнопке Назад")
    public TemplatesListPage backToTemplatesList() {
        backButton.click();
        return new TemplatesListPage();
    }

    @Step("Проверка валидации недопустимых значений в коде шаблона")
    public TemplatesListPage checkTemplateNameValidation(String[] names) {
        for (String name : names) {
            nameInput.setValue(name);
            TestUtils.wait(500);
            if (!templateNameValidationHint.exists()) {
                TestUtils.wait(500);
                nameInput.getInput().sendKeys("t");
            }
            templateNameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return new TemplatesListPage();
    }

    @Step("Проверка валидации некорректных параметров при создании шаблона")
    public TemplatesListPage checkCreateTemplateDisabled(Template template) {
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionTextArea.setValue(template.getDescription());
        runQueueInput.setValue(template.getRun());
        rollbackQueueInput.setValue(template.getRollback());
        typeSelect.set(template.getType());
        if (template.getTitle().isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (template.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (template.getRun().isEmpty()) {
            runQueueRequiredFieldHint.shouldBe(Condition.visible);
        }
        saveButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return new TemplatesListPage();
    }

    public TemplatesListPage checkNonUniqueNameValidation(Template template) {
        nameInput.setValue(template.getName());
        titleInput.setValue(template.getTitle());
        nonuniqueNameValidationHint.shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return new TemplatesListPage();
    }

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public TemplatePage checkUnsavedChangesAlertDismiss() {
        String newValue = "new";
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        templatesListLink.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        backButton.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        mainPage.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Ок")
    public TemplatePage checkUnsavedChangesAlertAccept(Template template) {
        String newValue = "new title";
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new TemplatesListPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        titleInput.setValue(newValue);
        templatesListLink.click();
        acceptAlert(unsavedChangesAlertText);
        new TemplatesListPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        titleInput.setValue(newValue);
        backButton.click();
        acceptAlert(unsavedChangesAlertText);
        new TemplatesListPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        titleInput.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToTemplatesPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        return this;
    }
}
