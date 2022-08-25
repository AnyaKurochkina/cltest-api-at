package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.elements.Input;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionPage {
    private final SelenideElement actionsListLink = $x("//a[text() = 'Список действий']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement info = $x("//div[@role = 'status']");
    private final SelenideElement inputDescriptionField = $x("//textarea[@name ='description']");
    private final SelenideElement locationInOrderTab = $x("//*[text()= 'Расположение в заказе']");
    private final SelenideElement graphTab = $x("//*[text()= 'Граф']");
    private final SelenideElement dataConfigPath = $x("//input[@name = 'data_config_path']");
    private final SelenideElement dataConfigKey = $x("//input[@name = 'data_config_key']");
    private final SelenideElement data = $x("//*[@placeholder = 'Введите данные через запятую']");
    private final SelenideElement saveButton = $x("//button/span[text() = 'Сохранить']");
    private final SelenideElement cancelButton = $x("//button/span[text() = 'Отмена']");
    private final SelenideElement inputGraphTitle = $x("//*[@id='selectValueWrapper']");
    private final SelenideElement deleteButton = $x("//span[text() ='Удалить']");
    private final SelenideElement currentVersionInput = $x("//label[starts-with(.,'Выберите версию')]/parent::*//input");
    private final SelenideElement deleteIconSvG = $x("(//div/*[local-name()='svg']/*[local-name()='svg']/*[local-name()='path'])[6]");
    private final SelenideElement addIcon = $x("//label[@for = 'attachment-input']");


    public ActionPage() {
        actionsListLink.shouldBe(Condition.visible);
        info.shouldBe(Condition.visible);
    }

    @Step("Возврат на страницу списка Действий")
    public ActionsListPage reTurnToActionsListPage() {
        cancelButton.click();
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
        DropDown.byLabel("Значение").selectByTitle(value);
        return this;
    }

    @Step("Сохранение действия")
    public SaveDialog saveAction() {
        saveButton.shouldBe(Condition.enabled).click();
        return new SaveDialog();
    }

    @Step("Проверка текущей версии")
    public void checkVersion(String version) {
        String currentVersion = currentVersionInput.getValue();
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
        inputNameField.shouldHave(Condition.exactValue(name));
        inputTitleField.shouldHave(Condition.exactValue(title));
        currentVersionInput.shouldHave(Condition.exactValue(version));
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
                                       String configPath, String configKey, String valueOfData, String graphTitle) {
        inputNameField.setValue(name);
        inputTitleField.setValue(title);
        inputDescriptionField.setValue(description);
        DropDown.byLabel("Обязательные статусы item").select(status.getValue());
        TestUtils.scrollToTheBottom();
        DropDown.byLabel("Обязательные статусы заказа").select(orderStatus.getValue());
        DropDown.byLabel("Тип").select(actionType.getValue());
        TestUtils.scrollToTheTop();
        locationInOrderTab.click();
        dataConfigPath.setValue(configPath);
        dataConfigKey.setValue(configKey);
        data.setValue(valueOfData);
        graphTab.click();
        inputGraphTitle.click();
        Input.byLabel("Граф").setValue(graphTitle);
        TestUtils.wait(1000);
        $x("//div[contains(@title, '" + graphTitle + "')]").click();
        TestUtils.scrollToTheBottom();
        saveButton.click();
        cancelButton.click();
        return new ActionsListPage();
    }
}
