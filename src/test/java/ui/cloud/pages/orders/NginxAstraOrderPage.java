package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

import static core.helper.StringUtils.$x;

@Getter
public class NginxAstraOrderPage extends NewOrderPage {

    Select platform = Select.byLabel("Платформа");
    Select osVersion = Select.byLabel("Версия ОС");
    Select group = Select.byLabel("Группы");
    Select groupWildFly = Select.byLabel("Группа управления WildFly");
    Select dataCentre = Select.byLabel("Дата-центр");
    Select segment = Select.byLabel("Сетевой сегмент");
    Select configure = Select.byLabel("Конфигурация Core/RAM");
    Input countVm = Input.byLabel("Количество");
    Input label = Input.byLabel("Метка");
    Input nameUser = Input.byLabel("Имя пользователя (админ с полными правами)");
    Input nameDB = Input.byLabel("Имя базы данных");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);
    SelenideElement generatePassButton1 = $x("//button[@aria-label='generate']");
    SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");

    public NginxAstraOrderPage() {
        label.setValue(labelValue);
        platform.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails(){
        if (getCalculationDetails().shouldBe(Condition.visible).exists()) {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getHardDrive().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
