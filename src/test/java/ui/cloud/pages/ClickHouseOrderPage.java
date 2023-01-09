package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.cloud.pages.Product;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

import static core.helper.StringUtils.$x;

@Getter
public class ClickHouseOrderPage extends Product {

    DropDown platform = DropDown.byLabel("Платформа");
    DropDown osVersion = DropDown.byLabel("Версия ОС");
    DropDown group = DropDown.byLabel("Группы");
    DropDown group2 = DropDown.byLabel("Группы",2);
    DropDown group3 = DropDown.byLabel("Группы",3);
    DropDown group4 = DropDown.byLabel("Группы",4);
    DropDown dataCentre = DropDown.byLabel("Дата-центр");
    DropDown segment = DropDown.byLabel("Сетевой сегмент");
    Select configure = Select.byLabel("Конфигурация Core/RAM");
    Input countVm = Input.byLabel("Количество");
    Input label = Input.byLabel("Метка");
    Input nameUser = Input.byLabel("Имя пользователя (админ с полными правами)");
    Input nameDB = Input.byLabel("Имя базы данных");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);
    SelenideElement generatePassButton1 = $x("//button[@aria-label='generate']");
    SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");

    public ClickHouseOrderPage() {
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
