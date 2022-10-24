package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.productCatalog.Service;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Input;
import ui.elements.TextArea;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicePage {

    private final SelenideElement serviceListLink = $x("//a[text()='Список сервисов' and not(@href)]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionInput = TextArea.byName("description");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final SelenideElement version = $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");

    public ServicePage() {
        serviceListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов сервиса '{service.serviceName}'")
    public ServicePage checkAttributes(Service service) {
        checkVersion(service.getVersion());
        nameInput.getInput().shouldHave(Condition.exactValue(service.getServiceName()));
        titleInput.getInput().shouldHave(Condition.exactValue(service.getTitle()));
        descriptionInput.getTextArea().shouldHave(Condition.exactValue(service.getDescription()));
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
        this.version.shouldHave(Condition.exactText(version));
        return this;
    }
}
