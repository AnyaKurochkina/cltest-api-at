package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.Switch;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class RedisAstraOrderPage extends NewOrderPage {

    private final Button generatePassButton = Button.byAriaLabel("generate");
    private final Input userInput = Input.byLabel("Пользователь");
    private final Switch createDefaultUserSwitch = Switch.byLabel("Создать пользователя default");
    SelenideElement hardDrive1 = $x("(//div[contains(text(),'Жесткий диск')])[1]");
    SelenideElement hardDrive2 = $x("(//div[contains(text(),'Жесткий диск')])[2]");
    Select redisVersion = Select.byLabel("Версия Redis");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public RedisAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        if (getCalculationDetails().shouldBe(Condition.visible).exists()) {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getHardDrive1().shouldBe(Condition.visible);
        getHardDrive2().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
