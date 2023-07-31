package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import static core.helper.StringUtils.$x;

@Getter
public class EtcdOrderPage extends NewOrderPage {

    Select numberNodes = Select.byLabel("Количество нод");
    Select configureCh = Select.byLabel("Конфигурация Core/RAM ClickHouse");
    Select configureZ = Select.byLabel("Конфигурация Core/RAM Zookeeper");
    Input nameUser = Input.byLabel("Пользователь etcd");
    Input nameCluster = Input.byLabel("Имя кластера");
    Input nameDB = Input.byLabel("Имя базы данных");
    SelenideElement generatePassButton = $x("//button[@aria-label='generate']");


    public EtcdOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
