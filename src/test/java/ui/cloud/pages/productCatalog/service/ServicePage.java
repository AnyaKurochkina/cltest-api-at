package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.productCatalog.Service;
import org.openqa.selenium.WebElement;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.TextArea;

import static com.codeborne.selenide.Selenide.$x;

public class ServicePage {

    private final SelenideElement serviceListLink = $x("//a[text()='Список сервисов' and not(@href)]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionInput = TextArea.byName("description");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final SelenideElement version = $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");
    private final SelenideElement saveButton = $x("//div[text()='Сохранить']/parent::button");
    private final String saveServiceAlertText = "Сервис успешно изменен";
    private final WebElement mainTab = $x("//button[span[text()='Основное']]");
    private final WebElement graphTab = $x("//button[span[text()='Граф']]");
    private final DropDown graphVersionDropDown = DropDown.byLabel("Значение");

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

    @Step("Сохранение шаблона со следующей патч-версией")
    public ServicePage saveWithPatchVersion() {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithNextPatchVersion(saveServiceAlertText);
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
}
