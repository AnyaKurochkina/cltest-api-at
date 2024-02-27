package ui.t1.pages.cdn.resource;

import io.qameta.allure.Step;
import ui.elements.Button;

public class ImageCompressingTab {

    private final Button activationButton = Button.byText("Подключить");
    private final Button offButton = Button.byText("Отключить");

    @Step("Клик по кнопке Подключение")
    public ImageCompressingTab activateImageCompressing() {
        activationButton.click();
        return this;
    }

    @Step("Выключение услуги")
    public ImageCompressingTab offImageCompressing() {
        offButton.click();
        return this;
    }
}
