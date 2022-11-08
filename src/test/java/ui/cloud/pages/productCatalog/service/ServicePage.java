package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.productCatalog.Service;
import org.openqa.selenium.WebElement;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.TextArea;

import static com.codeborne.selenide.Selenide.$x;

public class ServicePage extends BasePage {

    private final SelenideElement serviceListLink = $x("//a[text()='Список сервисов' and not(@href)]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionInput = TextArea.byName("description");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final SelenideElement version = $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");
    private final String saveServiceAlertText = "Сервис успешно изменен";
    private final WebElement mainTab = $x("//button[span[text()='Основное']]");
    private final WebElement paramsTab = $x("//button[span[text()='Параметры данных']]");
    private final WebElement graphTab = $x("//button[span[text()='Граф']]");
    private final DropDown graphVersionDropDown = DropDown.byLabel("Значение");
    private final TextArea extraData = TextArea.byLabel("Extra data");

    public ServicePage() {
        serviceListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов сервиса '{service.serviceName}'")
    public ServicePage checkAttributes(Service service) {
        checkVersion(service.getVersion());
        mainTab.click();
        nameInput.getInput().shouldHave(Condition.exactValue(service.getServiceName()));
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        descriptionInput.getTextArea().shouldHave(Condition.exactValue(service.getDescription()));
        graphTab.click();
        TestUtils.wait(2000);
        graphVersionDropDown.getElement().$x(".//div[@id='selectValueWrapper']")
                .shouldHave(Condition.exactText(service.getGraphVersion()));
        return this;
    }

    @Step("Редактирование атрибутов сервиса '{service.serviceName}'")
    public ServicePage setAttributes(Service service) {
        nameInput.setValue(service.getServiceName());
        titleInput.setValue(service.getTitle());
        descriptionInput.setValue(service.getDescription());
        graphTab.click();
        TestUtils.wait(2000);
        graphVersionDropDown.selectByTitle(service.getGraphVersion());
        return this;
    }

    @Step("Задание версии графа '{version}'")
    public ServicePage setGraphVersion(String version) {
        graphTab.click();
        TestUtils.wait(2000);
        graphVersionDropDown.selectByTitle(version);
        return this;
    }

    @Step("Задание значения Extra data")
    public ServicePage setExtraData(String value) {
        if (paramsTab.getAttribute("aria-selected").equals("false")) {
            paramsTab.click();
        }
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
        this.version.shouldHave(Condition.exactText(version));
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
}
