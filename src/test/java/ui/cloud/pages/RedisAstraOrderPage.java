package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class RedisAstraOrderPage extends NewOrderPage {

    SelenideElement hardDrive1 = $x("(//div[contains(text(),'Жесткий диск')])[1]");
    SelenideElement hardDrive2 = $x("(//div[contains(text(),'Жесткий диск')])[2]");
    Select redisVersion = Select.byLabel("Версия Redis");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public RedisAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails(){
        if(getCalculationDetails().shouldBe(Condition.visible).exists())
        {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getHardDrive1().shouldBe(Condition.visible);
        getHardDrive2().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
