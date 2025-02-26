package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.service.Service;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.back;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class ServicePage extends EntityPage {

    private final SelenideElement serviceListLink = $x("//a[text()='Список сервисов' and not(@href)]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionInput = TextArea.byName("description");
    private final String saveServiceAlertText = "Сервис успешно изменен";
    private final TextArea extraData = TextArea.byLabel("Extra data");
    private final String tagsTableTitle = "Теги";
    private final String excludeTagsTableTitle = "Исключающие теги";
    private final SelenideElement addServiceTagButton = $x("//div[text()='" + tagsTableTitle + "']/following::button[@label='Добавить'][1]");
    private final SelenideElement addExcludeTagButton = $x("//div[text()='" + excludeTagsTableTitle + "']/following::button[@label='Добавить'][1]");
    private final Select tagDropDown = Select.byLabel("Тег");
    private final Input tagValueInput = Input.byPlaceholder("Введите значение");
    private final SelenideElement addTagValueButton = $x("//div[@role='dialog']//input/..//button");
    private final SelenideElement addTagDialogSaveButton = $x("//div[@role='dialog']//button[div[text()='Сохранить']]");
    private final SelenideElement editTagMenuAction = $x("//div[@role='list'][not(@aria-hidden)]//li[.='Редактировать']");
    private final SelenideElement deleteTagMenuAction = $x("//div[@role='list'][not(@aria-hidden)]//li[.='Удалить']");
    private final SelenideElement deleteTagSubmitButton = $x("//form//button[@type='submit']");
    private final String tagTitleColumn = "Наименование";
    private final Switch isPublishedSwitch = Switch.byText("Опубликован");

    public ServicePage() {
        serviceListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов сервиса '{service.name}'")
    public ServicePage checkAttributes(Service service) {
        checkVersion(service.getVersion());
        goToMainTab();
        nameInput.getInput().shouldHave(Condition.exactValue(service.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        descriptionInput.getElement().shouldHave(Condition.exactValue(service.getDescription()));
        assertEquals(service.getIsPublished(), isPublishedSwitch.isEnabled());
        if (service.getGraphId() != null) {
            Graph graph = GraphSteps.getGraphById(service.getGraphId());
            goToGraphTab();
            TestUtils.wait(2000);
            assertTrue(graphSelect.getValue().contains(graph.getName()));
            assertEquals(service.getGraphVersion(), graphVersionSelect.getValue());
        }
        return this;
    }

    @Step("Редактирование атрибутов сервиса '{service.name}'")
    public ServicePage setAttributes(Service service) {
        nameInput.setValue(service.getName());
        titleInput.setValue(service.getTitle());
        descriptionInput.setValue(service.getDescription());
        isPublishedSwitch.getLabel().scrollIntoView(scrollCenter);
        isPublishedSwitch.setEnabled(service.getIsPublished());
        goToGraphTab();
        Waiting.sleep(2500);
        graphVersionSelect.set(service.getGraphVersion());
        return this;
    }

    @Step("Удаление иконки")
    public ServicePage deleteIcon() {
        deleteIconButton.click();
        saveWithoutPatchVersion(saveServiceAlertText);
        addIconLabel.shouldBe(Condition.visible);
        return this;
    }

    @Step("Задание версии графа '{version}'")
    public ServicePage setGraphVersion(String version) {
        goToGraphTab();
        TestUtils.wait(2000);
        graphVersionSelect.set(version);
        return this;
    }

    @Step("Задание значения Extra data")
    public ServicePage setExtraData(String value) {
        goToParamsTab();
        extraData.setValue(value);
        return this;
    }

    @Step("Сохранение сервиса со следующей патч-версией")
    public ServicePage saveWithPatchVersion() {
        super.saveWithPatchVersion(saveServiceAlertText);
        return this;
    }

    @Step("Сохранение сервиса с версией '{newVersion}'")
    public ServicePage saveWithManualVersion(String newVersion) {
        super.saveWithManualVersion(newVersion, saveServiceAlertText);
        return this;
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public ServicePage checkNextVersionAndSave(String nextVersion) {
        super.checkNextVersionAndSave(nextVersion, saveServiceAlertText);
        return this;
    }

    @Step("Удаление сервиса")
    public void deleteService() {
        deleteButton.click();
        new DeleteDialog().checkText("ВНИМАНИЕ! запланированные запуски будут отмененыКоличество запланированных запусков: 0")
                .inputValidIdAndDelete("Отложенные запуски отменены успешно");
        Alert.green("Удаление выполнено успешно");
    }

    @Step("Задание значения в поле 'Описание'")
    public ServicePage setDescription(String value) {
        descriptionInput.setValue(value);
        return this;
    }

    @Step("Проверка, что отображаемая версия равна '{version}'")
    public ServicePage checkVersion(String version) {
        Waiting.find(() -> versionSelect.getValue().equals(version), Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверка сохранения сервиса с некорректной версией '{newVersion}'")
    public ServicePage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        super.checkSaveWithInvalidVersion(newVersion, currentVersion);
        return this;
    }

    @Step("Проверка сохранения сервиса с версией некорректного формата '{newVersion}'")
    public ServicePage checkSaveWithInvalidVersionFormat(String newVersion) {
        super.checkSaveWithInvalidVersionFormat(newVersion);
        return this;
    }

    @Step("Переход на вкладку 'Основное'")
    public ServicePage goToMainTab() {
        super.goToTab("Основное");
        return this;
    }

    @Step("Переход на вкладку 'Граф'")
    public ServicePage goToGraphTab() {
        super.goToTab("Граф");
        return this;
    }

    @Step("Переход на вкладку 'Параметры данных'")
    public ServicePage goToParamsTab() {
        super.goToTab("Параметры данных");
        return this;
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public ServicePage goToVersionComparisonTab() {
        TestUtils.scrollToTheTop();
        getVersionComparisonTab().switchTo();
        return this;
    }

    @Step("Переход на вкладку 'Теги'")
    public ServicePage goToTagsTab() {
        super.goToTab("Теги");
        return this;
    }

    @Step("Добавление тега {tagName} со значениями {values}")
    public ServicePage addTag(String tagName, String[] values) {
        goToTagsTab();
        addServiceTagButton.click();
        tagDropDown.set(tagName);
        for (String value : values) {
            tagValueInput.setValue(value);
            addTagValueButton.click();
            $x("//span[text()='" + value + "']").shouldBe(Condition.visible);
        }
        addTagDialogSaveButton.click();
        return this;
    }

    @Step("Добавление исключающего тега {tagName} со значениями {values}")
    public ServicePage addExcludeTag(String tagName, String[] values) {
        goToTagsTab();
        addExcludeTagButton.scrollIntoView(true).click();
        tagDropDown.set(tagName);
        for (String value : values) {
            tagValueInput.setValue(value);
            addTagValueButton.click();
            $x("//span[text()='" + value + "']").shouldBe(Condition.visible);
        }
        addTagDialogSaveButton.click();
        return this;
    }

    @Step("Редактирование исключающего тега {tagName} и задание значений {values}")
    public ServicePage editExcludeTag(String tagName, String[] values) {
        goToTagsTab();
        Table table = new Table($x("//div[text()='" + excludeTagsTableTitle + "']/following::table[1]"));
        table.getRowByColumnValue(tagTitleColumn, tagName).get()
                .$x(".//button[@id='actions-menu-button']")
                .scrollIntoView(true)
                .click();
        editTagMenuAction.click();
        deleteAllTagValues();
        for (String value : values) {
            tagValueInput.setValue(value);
            addTagValueButton.click();
            $x("//span[text()='" + value + "']").shouldBe(Condition.visible);
        }
        addTagDialogSaveButton.click();
        return this;
    }

    @Step("Редактирование тега {tagName} и задание значений {values}")
    public ServicePage editTag(String tagName, String[] values) {
        goToTagsTab();
        Table table = new Table($x("//div[text()='" + tagsTableTitle + "']/following::table[1]"));
        table.getRowByColumnValue(tagTitleColumn, tagName).get()
                .$x(".//button[@id='actions-menu-button']")
                .click();
        editTagMenuAction.click();
        deleteAllTagValues();
        for (String value : values) {
            tagValueInput.setValue(value);
            addTagValueButton.click();
            $x("//span[text()='" + value + "']").shouldBe(Condition.visible);
        }
        addTagDialogSaveButton.click();
        return this;
    }

    @Step("Удаление тега {tagName}")
    public ServicePage deleteServiceTag(String tagName) {
        goToTagsTab();
        Table table = new Table($x("//div[text()='" + tagsTableTitle + "']/following::table[1]"));
        table.getRowByColumnValue(tagTitleColumn, tagName).get()
                .$x(".//button[@id='actions-menu-button']")
                .click();
        deleteTagMenuAction.click();
        deleteTagSubmitButton.click();
        return this;
    }

    @Step("Удаление исключающего тега {tagName}")
    public ServicePage deleteExcludeTag(String tagName) {
        goToTagsTab();
        Table table = new Table($x("//div[text()='" + excludeTagsTableTitle + "']/following::table[1]"));
        table.getRowByColumnValue(tagTitleColumn, tagName).get()
                .$x(".//button[@id='actions-menu-button']")
                .scrollIntoView(true)
                .click();
        deleteTagMenuAction.click();
        deleteTagSubmitButton.click();
        return this;
    }

    @Step("Проверка, что в таблице тегов отображается тег {tagName} со значениями {values}")
    public ServicePage checkTagsTable(String tagName, String[] values) {
        Table table = new Table($x("//div[text()='" + tagsTableTitle + "']/following::table[1]"));
        table.isColumnValueEquals(tagTitleColumn, tagName);
        for (String value : values) {
            table.getRowByColumnValue(tagTitleColumn, tagName).get()
                    .$x(".//td[3]/div[text()='" + value + "']").shouldBe(Condition.visible);
        }
        return this;
    }

    @Step("Проверка, что таблица тегов пустая")
    public ServicePage checkTagsTableIsEmpty() {
        Table table = new Table($x("//div[text()='" + tagsTableTitle + "']/following::table[1]"));
        assertTrue(table.isEmpty());
        return this;
    }

    @Step("Проверка, что таблица исключающих тегов пустая")
    public ServicePage checkExcludeTagsTableIsEmpty() {
        Table table = new Table($x("//div[text()='" + excludeTagsTableTitle + "']/following::table[1]"));
        assertTrue(table.isEmpty());
        return this;
    }

    @Step("Проверка, что в таблице исключающих тегов отображается тег {tagName} со значениями {values}")
    public ServicePage checkExcludeTagsTable(String tagName, String[] values) {
        Table table = new Table($x("//div[text()='" + excludeTagsTableTitle + "']/following::table[1]"));
        table.isColumnValueEquals(tagTitleColumn, tagName);
        for (String value : values) {
            table.getRowByColumnValue(tagTitleColumn, tagName).get()
                    .$x(".//td[3]/div[text()='" + value + "']").shouldBe(Condition.visible);
        }
        return this;
    }

    @Step("Проверка, что поле Extra data содержит значение {value}")
    public ServicePage checkDataSourceContainsValue(String value) {
        goToParamsTab();
        TestUtils.wait(1000);
        for (int i = 0; i < 5; i++) {
            if ($x("//label[text()='Data source']/following::span[text()='\"" + value + "\"']").isDisplayed())
                break;
            $x("//label[text()='Data source']/ancestor::div[2]//div[contains(@class,'view-line')][span][last()]")
                    .hover().scrollIntoView(true);
            i++;
        }
        $x("//label[text()='Data source']/following::span[text()='\"" + value + "\"']")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что поле Extra data не содержит значение {value}")
    public ServicePage checkDataSourceDoesNotContainValue(String value) {
        goToParamsTab();
        TestUtils.wait(1000);
        for (int i = 0; i < 5; i++) {
            if ($x("//label[text()='Data source']/following::span[text()='\"" + value + "\"']").isDisplayed()) {
                Assertions.fail("Отображается значение, которое должно отсутствовать: " + value);
            }
            $x("//label[text()='Data source']/ancestor::div[2]//div[contains(@class,'view-line')][span][last()]")
                    .hover().scrollIntoView(true);
            i++;
        }
        return this;
    }

    @Step("Удаление всех значений у тега")
    private void deleteAllTagValues() {
        while ($x("//span/following-sibling::*[name()='svg']").isDisplayed()) {
            $x("//span/following-sibling::*[name()='svg']").click();
        }
    }

    @Step("Возврат в список сервисов")
    public ServicesListPagePC goToServicesList() {
        TestUtils.scrollToTheTop();
        serviceListLink.click();
        return new ServicesListPagePC();
    }

    @Step("Отмена просмотра страницы сервиса")
    public ServicesListPagePC cancel() {
        cancelButton.click();
        return new ServicesListPagePC();
    }

    @Step("Назад в список сервисов")
    public ServicesListPagePC backToServicesList() {
        backButton.click();
        return new ServicesListPagePC();
    }

    @Step("Проверка недоступности удаления опубликованного сервиса")
    public void checkDeletePublishedService() {
        isPublishedSwitch.getLabel().scrollIntoView(true);
        isPublishedSwitch.setEnabled(true);
        deleteButton.getButton().hover();
        assertEquals("Недоступно для опубликованного сервиса", new Tooltip().toString());
        deleteButton.getButton().shouldBe(Condition.disabled);
        isPublishedSwitch.setEnabled(false);
        deleteButton.getButton().hover();
        Assertions.assertFalse(Tooltip.isVisible());
        deleteButton.getButton().shouldBe(Condition.enabled);
    }

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public ServicePage checkUnsavedChangesAlertDismiss() {
        String newValue = "new";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        serviceListLink.click();
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
    public ServicePage checkUnsavedChangesAlertAccept(Service service) {
        String newValue = "new title";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new ServicesListPagePC().openServicePage(service.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        titleInput.setValue(newValue);
        serviceListLink.click();
        acceptAlert(unsavedChangesAlertText);
        new ServicesListPagePC().openServicePage(service.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        titleInput.setValue(newValue);
        backButton.click();
        acceptAlert(unsavedChangesAlertText);
        new ServicesListPagePC().openServicePage(service.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        titleInput.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToServicesListPagePC().openServicePage(service.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        return this;
    }

    @Step("Переход на вкладку 'История изменений'")
    public AuditPage goToServiceAuditTab() {
        goToTab("История изменений");
        return new AuditPage();
    }
}
