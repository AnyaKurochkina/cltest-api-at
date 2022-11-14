package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.productCatalog.Service;
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
    private final SelenideElement addTagButton = $x("//div[text()='Теги']/following::button[@label='Добавить'][1]");
    private final SelenideElement addExcludeTagButton = $x("//div[text()='Исключающие теги']/following::button[@label='Добавить'][1]");
    private final DropDown tagDropDown = DropDown.byLabel("Тег");
    private final Input tagValueInput = Input.byPlaceholder("Введите значение");
    private final SelenideElement addTagValueButton = $x("//div[@role='dialog']//input/..//button");
    private final SelenideElement addTagDialogSaveButton = $x("//div[@role='dialog']//button[div[text()='Сохранить']]");
    private final String tagTitleColumn = "Наименование";

    public ServicePage() {
        serviceListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов сервиса '{service.serviceName}'")
    public ServicePage checkAttributes(Service service) {
        checkVersion(service.getVersion());
        goToVersionComparisonTab();
        nameInput.getInput().shouldHave(Condition.exactValue(service.getServiceName()));
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        descriptionInput.getTextArea().shouldHave(Condition.exactValue(service.getDescription()));
        if (service.getGraphId() != null) {
            goToGraphTab();
            TestUtils.wait(2000);
            graphDropDown.getElement().$x(".//div[@id='selectValueWrapper']")
                    .shouldHave(Condition.matchText(service.getGraph().getName()));
            graphVersionDropDown.getElement().$x(".//div[@id='selectValueWrapper']")
                    .shouldHave(Condition.exactText(service.getGraphVersion()));
        }
        return this;
    }

    @Step("Редактирование атрибутов сервиса '{service.serviceName}'")
    public ServicePage setAttributes(Service service) {
        nameInput.setValue(service.getServiceName());
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

    public ServicePage addTag(String tag, String[] values) {
        goToTagsTab();
        addTagButton.click();
        tagDropDown.selectByTitle(tag);
        for (String value : values) {
            tagValueInput.setValue(value);
            addTagValueButton.click();
            $x("//span[text()='" + value + "']").shouldBe(Condition.visible);
        }
        addTagDialogSaveButton.click();
        return this;
    }

    public ServicePage addExcludeTag(String tag, String[] values) {
        goToTagsTab();
        addExcludeTagButton.click();
        tagDropDown.selectByTitle(tag);
        for (String value : values) {
            tagValueInput.setValue(value);
            addTagValueButton.click();
            $x("//span[text()='" + value + "']").shouldBe(Condition.visible);
        }
        addTagDialogSaveButton.click();
        return this;
    }

    public ServicePage checkTagsTable(String tag, String[] values) {
        Table table = new Table($x("//div[text()='Теги']/following::table[1]"));
        table.isColumnValueEquals(tagTitleColumn, tag);
        for (String value : values) {
            table.getRowByIndex(0).$x(".//td[3]/div[text()='" + value + "']").shouldBe(Condition.visible);
        }
        return this;
    }

    public ServicePage checkExcludeTagsTable(String tag, String[] values) {
        Table table = new Table($x("//div[text()='Исключающие теги']/following::table[1]"));
        table.isColumnValueEquals(tagTitleColumn, tag);
        for (String value : values) {
            table.getRowByIndex(0).$x(".//td[3]/div[text()='" + value + "']").shouldBe(Condition.visible);
        }
        return this;
    }

    public ServicePage checkDataSourceContainsValue(String value) {
        goToParamsTab();
        TestUtils.wait(1000);
        while (!$x("//label[text()='Data source']/following::span[text()='\"" + value + "\"']").isDisplayed()) {
            $x("//label[text()='Data source']/ancestor::div[2]//div[contains(@class,'view-line')][span][last()]")
                    .hover().scrollIntoView(true);
        }
        $x("//label[text()='Data source']/following::span[text()='\"" + value + "\"']")
                .shouldBe(Condition.visible);
        return this;
    }
}
