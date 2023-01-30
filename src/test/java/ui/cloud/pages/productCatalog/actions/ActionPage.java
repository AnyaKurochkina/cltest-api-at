package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionPage extends BasePage {
    private static final String saveActionAlertText = "Действие успешно изменено";
    private final SelenideElement actionsListLink = $x("//a[text() = 'Список действий']");
    private final SelenideElement inputNameField = $x("//*[@name = 'name']");
    private final SelenideElement inputTitleField = $x("//*[@name = 'title']");
    private final SelenideElement info = $x("//div[@role = 'status']");
    private final SelenideElement inputDescriptionField = $x("//textarea[@name ='description']");
    private final SelenideElement locationInOrderTab = $x("//*[text()= 'Расположение в заказе']");
    private final SelenideElement graphTab = $x("//*[text() = 'Граф']");
    private final SelenideElement paramsTab = $x("//button[span[text()='Параметры']]");
    private final SelenideElement graphInputField = $x("//*[@id = 'selectValueWrapper']/input");
    private final Select graphVersionDropDown = Select.byLabel("Значение");
    private final SelenideElement dataConfigPath = $x("//input[@name = 'data_config_path']");
    private final SelenideElement dataConfigKey = $x("//input[@name = 'data_config_key']");
    private final SelenideElement data = $x("//*[@placeholder = 'Введите данные через запятую']");
    private final Input graphInput = Input.byLabelV2("Граф");
    private final SelenideElement deleteButton = $x("//div[text() ='Удалить']");
    private final SelenideElement currentVersionInput = $x("//label[text()='Выберите версию']/following::div[@id='selectValueWrapper']/div");
    private final SelenideElement addTypeAndProviderButton = $x("//button[div[text()='Добавить']]");
    private final Input priorityInput = Input.byName("priority");

    public ActionPage() {
        actionsListLink.shouldBe(Condition.visible);
        info.shouldBe(Condition.visible);
    }

    @Step("Возврат на страницу списка Действий через кнопку Отмена")
    public ActionsListPage reTurnToActionsListPageByCancelButton() {
        cancelButton.click();
        return new ActionsListPage();
    }

    @Step("Возврат в список действий по кнопке Назад")
    public ActionsListPage backToActionsList() {
        backButton.click();
        return new ActionsListPage();
    }

    @Step("Возврат на страницу списка Действий через ссылку Список дейтвий ")
    public ActionsListPage reTurnToActionsListPageByLink() {
        TestUtils.scrollToTheTop();
        actionsListLink.click();
        return new ActionsListPage();
    }

    @Step("Удаление из формы действий")
    public DeleteDialog deleteFromActionForm() {
        deleteButton.click();
        return new DeleteDialog();
    }

    @Step("Изменение версии графа")
    public ActionPage changeGraphVersion(String value) {
        graphTab.click();
        TestUtils.wait(2000);
        graphVersionDropDown.set(value);
        return this;
    }

    @Step("Сохранение действия без патч-версии")
    public ActionPage saveWithoutPatchVersion() {
        saveWithoutPatchVersion(saveActionAlertText);
        return this;
    }

    @Step("Сохранение действия со следующей патч версией")
    public ActionPage saveWithNextPatchVersion() {
        saveButton.click();
        new SaveDialog().saveWithNextPatchVersion(saveActionAlertText);
        return new ActionPage();
    }

    @Step("Сохранение действия с указанием версии")
    public ActionPage saveWithVersion(String newVersion) {
        saveButton.click();
        new SaveDialog().saveWithVersion(newVersion, saveActionAlertText);
        return new ActionPage();
    }

    @Step("Проверка сохранения действия с некорректно указанной версией '{newVersion}'")
    public ActionPage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return new ActionPage();
    }

    @Step("Проверка сохранения действия с указанной версией некорректного формата '{newVersion}'")
    public ActionPage checkSaveWithInvalidVersionFormat(String newVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersionFormat(newVersion);
        return new ActionPage();
    }

    @Step("Проверка текущей версии")
    public ActionPage checkVersion(String version) {
        String currentVersion = currentVersionInput.getText();
        assertEquals(version, currentVersion);
        return this;
    }

    @Step("Ввод значения в поле {label}")
    public ActionPage inputByLabel(String label, String value) {
        Input.byLabel(label).clear();
        Input.byLabel(label).setValue(value);
        return this;
    }

    @Step("Сравнение значений полей")
    public ActionPage compareFields(String name, String title, String version) {
        currentVersionInput.shouldHave(Condition.exactText(version));
        inputNameField.shouldHave(Condition.exactValue(name));
        inputTitleField.shouldHave(Condition.exactValue(title));
        return this;
    }

    @Step("Возврат на страницу списка действий через кнопку назад в браузере")
    public ActionsListPage backByBrowserButtonBack() {
        back();
        return new ActionsListPage();
    }

    @Step("Назад с помощью кнопки браузера и отмена оповещения о несохранненных данных")
    public ActionPage backOnBrowserAndAlertCancel() {
        back();
        String alertMsg = switchTo().alert().getText();
        assertEquals("Внесенные изменения не сохранятся. Покинуть страницу?", alertMsg);
        switchTo().alert().dismiss();
        actionsListLink.shouldBe(Condition.visible);
        return this;
    }

    @Step("Закрытие текущей вкладки и отмена оповещения о несохранненных данных")
    public ActionPage closeTabAndAlertCancel() {
        closeWindow();
        String alertMsg = switchTo().alert().getText();
        assertEquals("Возможно, внесенные изменения не сохранятся.", alertMsg);
        switchTo().alert().dismiss();
        actionsListLink.shouldBe(Condition.visible);
        return this;
    }

    @Step("Переход на список действий и отмена оповещения о несохранненных данных")
    public ActionPage backByActionsLinkAndAlertCancel() {
        actionsListLink.scrollIntoView(TypifiedElement.scrollCenter);
        actionsListLink.click();
        String alertMsg = switchTo().alert().getText();
        assertEquals("Внесенные изменения не сохранятся. Покинуть страницу?", alertMsg);
        switchTo().alert().dismiss();
        actionsListLink.shouldBe(Condition.visible);
        return this;
    }

    @Step("Удаление иконки")
    public ActionPage deleteIcon() {
        deleteIconButton.click();
        addIconLabel.shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка отсутствия иконки")
    public boolean isIconExist() {
        return deleteIconButton.is(Condition.visible);
    }

    @Step("Запонение полей для создания действия и сохранение")
    public ActionsListPage fillAndSave(String name, String title, String description, ItemStatus status, OrderStatus orderStatus, ActionType actionType,
                                       String configPath, String configKey, String valueOfData, String graphTitle, EventType eventType, EventProvider eventProvider) {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        inputNameField.setValue(name);
        inputTitleField.setValue(title);
        inputDescriptionField.setValue(description);
        MultiSelect.byLabel("Обязательные статусы item").set(status.getValue());
        MultiSelect.byLabel("Обязательные статусы заказа").set(orderStatus.getValue());
        Select.byLabel("Тип").set(actionType.getValue());
        paramsTab.scrollIntoView(false).click();
        addTypeAndProviderButton.click();
        Table table = new Table("Тип");
        Select eventTypeDropDown = new Select(table.getRow(0).get().$x("(.//div[select])[1]"));
        Select eventProviderDropDown = new Select(table.getRow(0).get().$x("(.//div[select])[2]"));
        //TODO DropDown не сразу раскрывается
        for (int i = 0; i < 10; i++) {
            eventTypeDropDown.getElement().$x(".//*[name()='svg']").click();
            TestUtils.wait(1500);
            if ($x("//div[text() = '" + eventType.getValue() + "']").isDisplayed()) break;
        }
        $x("//div[text() = '" + eventType.getValue() + "']").click();
        eventProviderDropDown.set(eventProvider.getValue());
        table.getRow(0).get().$x(".//button[.//*[text() = 'Сохранить']]").click();
        locationInOrderTab.click();
        dataConfigPath.setValue(configPath);
        dataConfigKey.setValue(configKey);
        data.setValue(valueOfData);
        graphTab.click();
        graphInput.click();
        graphInputField.setValue(graphTitle);
        TestUtils.wait(1000);
        $x("//div[contains(text(), '" + graphTitle + "')]").click();
        TestUtils.scrollToTheBottom();
        saveButton.click();
        backButton.click();
        return new ActionsListPage();
    }

    @Step("Задание атрибута Приоритет сообщения = {priority}")
    public ActionPage setPriority(int priority) {
        priorityInput.setValue(priority);
        return this;
    }

    @Step("Сохранение действия со следующей патч-версией")
    public ActionPage saveWithPatchVersion() {
        saveWithPatchVersion(saveActionAlertText);
        return this;
    }

    @Step("Сохранение действия с версией '{newVersion}'")
    public ActionPage saveWithManualVersion(String newVersion) {
        saveWithManualVersion(newVersion, saveActionAlertText);
        return this;
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public ActionPage checkNextVersionAndSave(String nextVersion) {
        checkNextVersionAndSave(nextVersion, saveActionAlertText);
        return this;
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public ActionPage goToVersionComparisonTab() {
        goToTab("Сравнение версий");
        return this;
    }
}
