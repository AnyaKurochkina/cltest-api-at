package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import static core.helper.StringUtils.$x;

@Getter
public class RabbitMqClusterAstraOrderPage extends NewOrderPage {

    Select numberNodes = Select.byLabel("Количество нод кластера по типу");
    Select configureCh = Select.byLabel("Конфигурация Core/RAM ClickHouse");
    Select configureZ = Select.byLabel("Конфигурация Core/RAM Zookeeper");
    Input nameUser = Input.byLabel("Имя пользователя (админ с полными правами)");
    Input nameCluster = Input.byLabel("Имя кластера");
    Input nameDB = Input.byLabel("Имя базы данных");
    Select group2 = Select.byXpath("(//div[label[text()='Группы']]/div)[2]");
    Select group3 = Select.byXpath("(//div[label[text()='Группы']]/div)[3]");
    Select group4 = Select.byXpath("(//div[label[text()='Группы']]/div)[4]");
    SelenideElement generatePassButton1 = $x("//button[@aria-label='generate']");
    SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");

    public RabbitMqClusterAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
