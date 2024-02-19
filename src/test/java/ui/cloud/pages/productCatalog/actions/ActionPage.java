package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.getGraphById;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class ActionPage extends EntityPage {
    private final String saveActionAlertText = "Действие успешно изменено";
    private final SelenideElement actionsListLink = $x("//a[text() = 'Список действий']");
    private final SelenideElement info = $x("//div[@role = 'status']");
    private final TextArea descriptionTextArea = TextArea.byName("description");
    private final SelenideElement locationInOrderTab = $x("//*[text()= 'Расположение в заказе']");
    private final SelenideElement graphTab = $x("//*[text() = 'Граф']");
    private final SelenideElement paramsTab = $x("//button[span[text()='Параметры']]");
    private final SelenideElement dataConfigPath = $x("//input[@name = 'data_config_path']");
    private final SelenideElement dataConfigKey = $x("//input[@name = 'data_config_key']");
    private final SelenideElement data = $x("//*[@placeholder = 'Введите данные через запятую']");
    private final Input graphInput = Input.byLabelV2("Граф");
    private final SelenideElement currentVersionInput = $x("//label[text()='Выберите версию']/following::div[@id='selectValueWrapper']/div");
    private final SelenideElement addTypeAndProviderButton = $x("//button[div[text()='Добавить']]");
    private final Input priorityInput = Input.byName("priority");
    private final MultiSelect requiredItemStatusesSelect = MultiSelect.byLabel("Обязательные статусы item");
    private final MultiSelect requiredOrderStatusesSelect = MultiSelect.byLabel("Обязательные статусы заказа");
    private final Button registerButton = Button.byText("Зарегистрировать");
    private final Tab objectInfoTab = Tab.byText("Информация о действии");

    public ActionPage() {
        actionsListLink.shouldBe(Condition.visible);
        info.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов действия '{action.name}'")
    public ActionPage checkAttributes(Action action) {
        checkVersion(action.getVersion());
        goToMainTab();
        nameInput.getInput().shouldHave(Condition.exactValue(action.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(action.getTitle()));
        descriptionTextArea.getElement().shouldHave(Condition.exactValue(action.getDescription()));
        goToGraphTab();
        Graph graph = getGraphById(action.getGraphId());
        Waiting.find(() -> graphSelect.getValue().contains(graph.getName()), Duration.ofSeconds(5));
        Assertions.assertEquals(action.getGraphVersion(), graphVersionSelect.getValue());
        objectInfoTab.switchTo();
        assertEquals(action.getObject_info(), objectInfoEditor.getText());
        return this;
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
        graphVersionSelect.set(value);
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

    @Step("Проверка отображаемой версии")
    public ActionPage checkVersion(String version) {
        Waiting.find(() -> versionSelect.getValue().equals(version), Duration.ofSeconds(5));
        return this;
    }

    @Step("Сравнение значений полей")
    public ActionPage compareFields(String name, String title, String version) {
        currentVersionInput.shouldHave(Condition.exactText(version));
        nameInput.getInput().shouldHave(Condition.exactValue(name));
        titleInput.getInput().shouldHave(Condition.exactValue(title));
        return this;
    }

    @Step("Возврат на страницу списка действий через кнопку назад в браузере")
    public ActionsListPage backByBrowserButtonBack() {
        back();
        return new ActionsListPage();
    }

    @Step("Переход на список действий и отмена оповещения о несохранненных данных")
    public ActionPage backByActionsLinkAndAlertCancel() {
        actionsListLink.scrollIntoView(scrollCenter);
        actionsListLink.click();
        String alertMsg = switchTo().alert().getText();
        assertEquals("Внесенные изменения не сохранятся. Покинуть страницу?", alertMsg);
        switchTo().alert().dismiss();
        actionsListLink.shouldBe(Condition.visible);
        return this;
    }

    @Step("Удаление иконки")
    public ActionPage deleteIcon() {
        deleteIconButton.scrollIntoView(scrollCenter).click();
        addIconLabel.shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка отсутствия иконки")
    public boolean isIconExist() {
        return deleteIconButton.is(Condition.visible);
    }

    @Step("Запонение полей для создания действия и сохранение")
    public ActionsListPage setAttributesAndSave(Action action) {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        nameInput.setValue(action.getName());
        titleInput.setValue(action.getTitle());
        descriptionTextArea.setValue(action.getDescription());
        requiredItemStatusesSelect.set(action.getRequiredItemStatuses().get(0));
        requiredOrderStatusesSelect.set(action.getRequiredOrderStatuses().get(0).toString());
        Select.byName("type").set(action.getType());
        paramsTab.scrollIntoView(false).click();
        addTypeAndProviderButton.click();
        Table table = new Table("Тип");
        Select eventTypeDropDown = new Select(table.getRow(0).get().$x("(.//div[select])[1]"));
        Select eventProviderDropDown = new Select(table.getRow(0).get().$x("(.//div[select])[2]"));
        String eventType = action.getEventTypeProvider().get(0).getEvent_type();
        //TODO DropDown не сразу раскрывается
        for (int i = 0; i < 10; i++) {
            eventTypeDropDown.getElement().$x(".//*[name()='svg']").click();
            Waiting.sleep(1500);
            if ($x("//div[. = '" + eventType + "']").isDisplayed()) break;
        }
        $x("//div[. = '" + eventType + "']").click();
        eventProviderDropDown.set(action.getEventTypeProvider().get(0).getEvent_provider());
        table.getRow(0).get().$x(".//button[.='Сохранить']").click();
        locationInOrderTab.click();
        dataConfigPath.setValue(action.getDataConfigPath());
        dataConfigKey.setValue(action.getDataConfigKey());
        data.setValue(action.getDataConfigFields().get(0).toString());
        graphTab.click();
        Graph graph = getGraphById(action.getGraphId());
        graphSelect.setContains(graph.getTitle());
        Waiting.find(() -> graphSelect.getValue().contains(graph.getName()), Duration.ofSeconds(15));
        graphVersionSelect.set(action.getGraphVersion());
        objectInfoTab.switchTo();
        objectInfoEditor.setValue(action.getObject_info());
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
        goToTab("Сравнение");
        return this;
    }

    public ActionPage goToMainTab() {
        goToTab("Основное");
        return this;
    }

    public ActionPage goToGraphTab() {
        goToTab("Граф");
        return this;
    }

    public ActionPage goToAdditionalParamsTab() {
        goToTab("Дополнительные параметры");
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public ActionPage checkUnsavedChangesAlertDismiss() {
        String newValue = "new";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        actionsListLink.scrollIntoView(false).click();
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
    public ActionPage checkUnsavedChangesAlertAccept(Action action) {
        String newValue = "new title";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new ActionsListPage().openActionPage(action.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(action.getTitle()));
        titleInput.setValue(newValue);
        actionsListLink.scrollIntoView(false).click();
        acceptAlert(unsavedChangesAlertText);
        new ActionsListPage().openActionPage(action.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(action.getTitle()));
        titleInput.setValue(newValue);
        backButton.click();
        acceptAlert(unsavedChangesAlertText);
        new ActionsListPage().openActionPage(action.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(action.getTitle()));
        titleInput.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToActionsListPage().openActionPage(action.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(action.getTitle()));
        return this;
    }

    @Step("Выбор графа '{name}'")
    public ActionPage setGraph(String name) {
        goToGraphTab();
        graphSelect.setContains(name);
        return this;
    }

    @Step("Проверка отсутствия графа '{name}' в доступных для выбора")
    public void checkGraphNotFound(String name) {
        goToGraphTab();
        graphSelect.checkNoMatches(name);
    }
}
