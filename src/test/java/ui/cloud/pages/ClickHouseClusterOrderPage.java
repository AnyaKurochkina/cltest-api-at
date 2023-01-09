package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

import static core.helper.StringUtils.$x;

@Getter
public class ClickHouseClusterOrderPage extends Product {

    DropDown platform = DropDown.byLabel("Платформа");
    DropDown osVersion = DropDown.byLabel("Версия ОС");
    DropDown group = DropDown.byLabel("Группы");
    DropDown dataCentre = DropDown.byLabel("Дата-центр");
    DropDown segment = DropDown.byLabel("Сетевой сегмент");
    DropDown numberNodes = DropDown.byLabel("Количество нод кластера по типу");
    DropDown configureCh = DropDown.byLabel("Конфигурация Core/RAM ClickHouse");
    DropDown configureZ = DropDown.byLabel("Конфигурация Core/RAM Zookeeper");
    Input countVm = Input.byLabel("Количество");
    Input label = Input.byLabel("Метка");
    Input nameUser = Input.byLabel("Имя пользователя (админ с полными правами)");
    Input nameCluster = Input.byLabel("Имя кластера");
    Input nameDB = Input.byLabel("Имя базы данных");
    DropDown group2 = DropDown.byXpath("(//div[label[text()='Группы']]/div)[2]");
    DropDown group3 = DropDown.byXpath("(//div[label[text()='Группы']]/div)[3]");
    DropDown group4 = DropDown.byXpath("(//div[label[text()='Группы']]/div)[4]");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);
    SelenideElement generatePassButton1 = $x("//button[@aria-label='generate']");
    SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");



    public ClickHouseClusterOrderPage() {
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
