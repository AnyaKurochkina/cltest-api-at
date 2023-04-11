package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.Switch;

@Getter
public class RedisAstraOrderPage extends NewOrderPage {

    private final Button generatePassButton = Button.byAriaLabel("generate");
    private final Input userInput = Input.byLabel("Пользователь");
    private final Switch createDefaultUserSwitch = Switch.byText("Создать пользователя default");
    Select redisVersion = Select.byLabel("Версия Redis");

    public RedisAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrive2().shouldBe(Condition.visible);
    }
}
