package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class ApacheKafkaClusterOrderPage extends Product {

    SelenideElement hardDrive1 = $x("(//div[contains(text(),'Жесткий диск')])[1]");
    SelenideElement hardDrive2 = $x("(//div[contains(text(),'Жесткий диск')])[2]");
    Input nameCluster = Input.byLabel("Имя кластера *");
    DropDown platform = DropDown.byLabel("Платформа");
    DropDown osVersion = DropDown.byLabel("Версия ОС");
    DropDown group = DropDown.byLabel("Группы");
    DropDown dataCentre = DropDown.byLabel("Дата-центр");
    DropDown segment = DropDown.byLabel("Сетевой сегмент");
    DropDown configure = DropDown.byLabel("Конфигурация Core/RAM");
    Input countVm = Input.byLabel("Количество");
    Input label = Input.byLabel("Метка");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public ApacheKafkaClusterOrderPage() {
        label.setValue(labelValue);
        platform.getElement().shouldBe(Condition.enabled);
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
