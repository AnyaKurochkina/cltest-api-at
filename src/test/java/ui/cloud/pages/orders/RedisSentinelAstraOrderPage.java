package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.Switch;

@Getter
public class RedisSentinelAstraOrderPage extends NewOrderPage {

    private final Button generatePassButton = Button.byAriaLabel("generate");
    private final Input userInput = Input.byLabel("Пользователь");
    private final Switch createDefaultUserSwitch = Switch.byText("Создать пользователя default");
    private final Select redisVersion = Select.byLabel("Версия Redis");
    public static String userNameRedisSentinel = "sentinel";
    protected Select flavorSelectRedisSentinel = Select.byLabel("Конфигурация мастера и реплики (Core/RAM)");

    public RedisSentinelAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
