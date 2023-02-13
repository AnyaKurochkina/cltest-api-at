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
public class S3CephTenantOrderPage extends Product {

    DropDown platform = DropDown.byLabel("Платформа");
    DropDown osVersion = DropDown.byLabel("Версия ОС");
    DropDown redisVersion = DropDown.byLabel("Версия Redis");
    DropDown group = DropDown.byLabel("Группы");
    DropDown dataCentre = DropDown.byLabel("Дата-центр");
    DropDown segment = DropDown.byLabel("Сетевой сегмент");
    Select configure = Select.byLabel("Конфигурация Core/RAM");
    Input countVm = Input.byLabel("Количество");
    Input label = Input.byLabel("Метка");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public S3CephTenantOrderPage() {
        label.setValue(labelValue);
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
