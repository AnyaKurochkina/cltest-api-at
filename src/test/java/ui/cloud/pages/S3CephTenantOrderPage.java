package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Select;
import ui.extesions.UiProductTest;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class S3CephTenantOrderPage extends NewOrderPage {

    Select redisVersion = Select.byLabel("Версия Redis");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public S3CephTenantOrderPage() {
        labelInput.setValue(labelValue);
    }

    public void checkOrderDetails(){
        if(getCalculationDetails().shouldBe(Condition.visible).exists())
        {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
