package ui.t1.pages.cdn.resource;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.*;

public class ShieldingOfSourcesTab {

    private final Button activationButton = Button.byText("Подключить");
    private final Dialog activationModal = Dialog.byTitle("Настройка экранирования");
    private final Button editButton = Button.byText("Редактировать");
    private final SelenideElement offToggle = Selenide.$x("//label[@role='switch']");

    @Step("Подключение и проверка алерта")
    public ShieldingOfSourcesTab activateShieldingWithLocation(String location) {
        activationButton.click();
        activationModal.setDropdown(Select.byName("shielding_pop"), location);
        activationModal.clickButton("Сохранить");
        Alert.green("Экранирующий сервер подключен");
        return this;
    }

    @Step("[Проверка] Экранирование подключено с правильной локацией")
    public ShieldingOfSourcesTab checkShieldingIsActivatedWithLocation(String location) {
        new Table("").getRow(1).asserts().checkLastValueOfRowContains(location);
        return this;
    }

    @Step("Редактирование локации")
    public ShieldingOfSourcesTab changeLocation(String location) {
        editButton.click();
        activationModal.setDropdown(Select.byName("shielding_pop"), location);
        activationModal.clickButton("Сохранить");
        Alert.green("Экранирующий сервер подключен");
        return this;
    }

    @Step("Выключение услуги")
    public ShieldingOfSourcesTab offShielding() {
        offToggle.click();
        return this;
    }

    @Step("[Проверка] Услуга Экранирование источников отключена")
    public ShieldingOfSourcesTab checkThatShieldingIsOff() {
        activationButton.isVisible();
        return this;
    }
}
