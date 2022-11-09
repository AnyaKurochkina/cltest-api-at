package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.SaveDialog;
import models.productCatalog.enums.EventProvider;
import models.productCatalog.enums.EventType;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionPage {
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
    private final DropDown graphVersionDropDown = DropDown.byLabel("Значение");
    private final SelenideElement dataConfigPath = $x("//input[@name = 'data_config_path']");
    private final SelenideElement dataConfigKey = $x("//input[@name = 'data_config_key']");
    private final SelenideElement data = $x("//*[@placeholder = 'Введите данные через запятую']");
    private final SelenideElement saveButton = $x("//button/div[text() = 'Сохранить']");
    private final SelenideElement cancelButton = $x("//button/div[text() = 'Отмена']");
    private final Input graphInput = Input.byLabelV2("Граф");
    private final SelenideElement deleteButton = $x("//div[text() ='Удалить']");
    private final SelenideElement currentVersionInput = $x("//label[text()='Выберите версию']/following::div[@id='selectValueWrapper']/div");
    private final SelenideElement deleteIconSvG = $x("(//div/*[local-name()='svg']/*[local-name()='svg']/*[local-name()='path'])[3]");
    private final SelenideElement addIcon = $x("//label[@for = 'attachment-input']");
    private final SelenideElement addTypeAndProviderButton = $x("//button[div[text()='Добавить']]");


    public ActionPage() {
        actionsListLink.shouldBe(Condition.visible);
        info.shouldBe(Condition.visible);
    }

    @Step("Возврат на страницу списка Действий через кнопку Отмена")
    public ActionsListPage reTurnToActionsListPageByCancelButton() {
        cancelButton.click();
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
        graphVersionDropDown.selectByTitle(value);
        return this;
    }

    @Step("Сохранение действия со следующей патч версией")
    public ActionPage saveWithNextPatchVersion() {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithNextPatchVersion(saveActionAlertText);
        return new ActionPage();
    }

    @Step("Сохранение действия с указанием версии")
    public ActionPage saveWithVersion(String newVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithVersion(newVersion, saveActionAlertText);
        return new ActionPage();
    }

    @Step("Проверка сохранения действия с некорректно указанной версией '{newVersion}'")
    public ActionPage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return new ActionPage();
    }

    @Step("Проверка сохранения действия с указанной версией некорректного формата '{newVersion}'")
    public ActionPage checkSaveWithInvalidVersionFormat(String newVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkSaveWithInvalidVersionFormat(newVersion);
        return new ActionPage();
    }

    @Step("Проверка текущей версии")
    public void checkVersion(String version) {
        String currentVersion = currentVersionInput.getText();
        assertEquals(version, currentVersion);
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

    @Step("Назад с помощью кнопки броузера и отмена оповещения о несохранненных данных")
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
        deleteIconSvG.click();
        addIcon.shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка отсутствия иконки")
    public boolean isIconExist() {
        return deleteIconSvG.is(Condition.visible);
    }

    @Step("Запонение полей для создания действия и сохранение")
    public ActionsListPage fillAndSave(String name, String title, String description, ItemStatus status, OrderStatus orderStatus, ActionType actionType,
                                       String configPath, String configKey, String valueOfData, String graphTitle, EventType eventType, EventProvider eventProvider) {
        inputNameField.setValue(name);
        inputTitleField.setValue(title);
        inputDescriptionField.setValue(description);
        DropDown.byLabel("Обязательные статусы item").select(status.getValue());
        TestUtils.scrollToTheBottom();
        DropDown.byLabel("Обязательные статусы заказа").select(orderStatus.getValue());
        DropDown.byLabel("Тип").select(actionType.getValue());
        TestUtils.scrollToTheTop();
        paramsTab.click();
        addTypeAndProviderButton.click();
        Table table = new Table("Тип");
        DropDown eventTypeDropDown = new DropDown(table.getRowByIndex(0).$x("(.//div[select])[1]"));
        DropDown eventProviderDropDown = new DropDown(table.getRowByIndex(0).$x("(.//div[select])[2]"));
        eventTypeDropDown.click();
        //TODO DropDown не сразу раскрывается
        for (int i = 0; i < 10; i++) {
            eventTypeDropDown.getElement().$x(".//*[name()='svg']").click();
            TestUtils.wait(1500);
            if ($x("//div[@title = '" + eventType.getValue() + "']").isDisplayed()) break;
        }
        $x("//div[@title = '" + eventType.getValue() + "']").click();
        eventProviderDropDown.selectByTitle(eventProvider.getValue());
        table.getRowByIndex(0).$x(".//button[.//*[text() = 'Сохранить']]").click();
        locationInOrderTab.click();
        dataConfigPath.setValue(configPath);
        dataConfigKey.setValue(configKey);
        data.setValue(valueOfData);
        graphTab.click();
        graphInput.click();
        graphInputField.setValue(graphTitle);
        TestUtils.wait(1000);
        $x("//div[contains(@title, '" + graphTitle + "')]").click();
        TestUtils.scrollToTheBottom();
        saveButton.click();
        cancelButton.click();
        return new ActionsListPage();
    }
}
