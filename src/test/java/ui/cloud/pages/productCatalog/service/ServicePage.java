package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.service.Service;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;
import ui.elements.TextArea;

import static com.codeborne.selenide.Selenide.$x;

public class ServicePage extends BasePage {

    private final SelenideElement serviceListLink = $x("//a[text()='Список сервисов' and not(@href)]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionInput = TextArea.byName("description");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final String saveServiceAlertText = "Сервис успешно изменен";
    private final DropDown graphDropDown = DropDown.byLabel("Граф");
    private final DropDown graphVersionDropDown = DropDown.byLabel("Значение");
    private final TextArea extraData = TextArea.byLabel("Extra data");
    private final String tagsTableTitle = "Теги";
    private final String excludeTagsTableTitle = "Исключающие теги";
    private final SelenideElement addTagButton = $x("//div[text()='" + tagsTableTitle + "']/following::button[@label='Добавить'][1]");
    private final SelenideElement addExcludeTagButton = $x("//div[text()='" + excludeTagsTableTitle + "']/following::button[@label='Добавить'][1]");
    private final DropDown tagDropDown = DropDown.byLabel("Тег");
    private final Input tagValueInput = Input.byPlaceholder("Введите значение");
    private final SelenideElement addTagValueButton = $x("//div[@role='dialog']//input/..//button");
    private final SelenideElement addTagDialogSaveButton = $x("//div[@role='dialog']//button[div[text()='Сохранить']]");
    private final SelenideElement editTagMenuAction = $x("//div[@role='list'][not(@aria-hidden)]//li[contains(text(),'Редактировать')]");
    private final SelenideElement deleteTagMenuAction = $x("//div[@role='list'][not(@aria-hidden)]//li[contains(text(),'Удалить')]");
    private final SelenideElement deleteTagSubmitButton = $x("//form//button[@type='submit']");
    private final String tagTitleColumn = "Наименование";
    private final String noDataFound = "Нет данных для отображения";

    public ServicePage() {
        serviceListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов сервиса '{service.name}'")
    public ServicePage checkAttributes(Service service) {
        checkVersion(service.getVersion());
        goToMainTab();
        nameInput.getInput().shouldHave(Condition.exactValue(service.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        descriptionInput.getTextArea().shouldHave(Condition.exactValue(service.getDescription()));
        if (service.getGraphId() != null) {
            Graph graph = GraphSteps.getGraphById(service.getGraphId());
            goToGraphTab();
            TestUtils.wait(2000);
            graphDropDown.getElement().$x(".//div[@id='selectValueWrapper']")
                    .shouldHave(Condition.matchText(graph.getName()));
            graphVersionDropDown.getElement().$x(".//div[@id='selectValueWrapper']")
                    .shouldHave(Condition.exactText(service.getGraphVersion()));
        }
        return this;
    }

    @Step("Редактирование атрибутов сервиса '{service.name}'")
    public ServicePage setAttributes(Service service) {
        nameInput.setValue(service.getName());
        titleInput.setValue(service.getTitle());
        descriptionInput.setValue(service.getDescription());
        goToGraphTab();
        TestUtils.wait(2000);
        graphVersionDropDown.selectByTitle(service.getGraphVersion());
        return this;
    }

    @Step("Задание версии графа '{version}'")
    public ServicePage setGraphVersion(String version) {
        goToGraphTab();
        TestUtils.wait(2000);
        graphVersionDropDown.selectByTitle(version);
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
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
    }

    @Step("Задание значения в поле 'Описание'")
    public ServicePage setDescription(String value) {
        descriptionInput.setValue(value);
        return this;
    }

    @Step("Проверка, что отображаемая версия равна '{version}'")
    public ServicePage checkVersion(String version) {
        TestUtils.scrollToTheTop();
        this.selectedVersion.shouldHave(Condition.exactText(version));
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
        versionComparisonTab.click();
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
        addTagButton.click();
        tagDropDown.selectByTitle(tagName);
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
        addExcludeTagButton.click();
        tagDropDown.selectByTitle(tagName);
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
        table.getRowElementByColumnValue(tagTitleColumn, tagName)
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
        table.getRowElementByColumnValue(tagTitleColumn, tagName)
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
    public ServicePage deleteTag(String tagName) {
        goToTagsTab();
        Table table = new Table($x("//div[text()='" + tagsTableTitle + "']/following::table[1]"));
        table.getRowElementByColumnValue(tagTitleColumn, tagName)
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
        table.getRowElementByColumnValue(tagTitleColumn, tagName)
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
            table.getRowElementByColumnValue(tagTitleColumn, tagName).$x(".//td[3]/div[text()='" + value + "']").shouldBe(Condition.visible);
        }
        return this;
    }

    @Step("Проверка, что таблица тегов пустая")
    public ServicePage checkTagsTableIsEmpty() {
        Table table = new Table($x("//div[text()='" + tagsTableTitle + "']/following::table[1]"));
        Assertions.assertEquals(noDataFound, table.getRowByIndex(0).getText());
        return this;
    }

    @Step("Проверка, что таблица исключающих тегов пустая")
    public ServicePage checkExcludeTagsTableIsEmpty() {
        Table table = new Table($x("//div[text()='" + excludeTagsTableTitle + "']/following::table[1]"));
        Assertions.assertEquals(noDataFound, table.getRowByIndex(0).getText());
        return this;
    }

    @Step("Проверка, что в таблице исключающих тегов отображается тег {tagName} со значениями {values}")
    public ServicePage checkExcludeTagsTable(String tagName, String[] values) {
        Table table = new Table($x("//div[text()='" + excludeTagsTableTitle + "']/following::table[1]"));
        table.isColumnValueEquals(tagTitleColumn, tagName);
        for (String value : values) {
            table.getRowElementByColumnValue(tagTitleColumn, tagName).$x(".//td[3]/div[text()='" + value + "']").shouldBe(Condition.visible);
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
}
