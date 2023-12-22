package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import static core.helper.StringUtils.$x;

@Getter
public class ClickHouseClusterOrderPage extends NewOrderPage {

    private final Select numberNodes = Select.byLabel("Количество нод кластера по типу");
    private final Select configureCh = Select.byLabel("Конфигурация Core/RAM ClickHouse");
    private final Select configureZ = Select.byLabel("Конфигурация Core/RAM Zookeeper");
    private final Input nameUser = Input.byLabel("Имя пользователя (админ с полными правами)");
    private final Input nameCluster = Input.byLabel("Имя кластера");
    private final Input nameDB = Input.byLabel("Имя базы данных");
    private final Select group2 = Select.byXpath("(//div[label[text()='Группы']]/div)[2]");
    private final Select group3 = Select.byXpath("(//div[label[text()='Группы']]/div)[3]");
    private final Select group4 = Select.byXpath("(//div[label[text()='Группы']]/div)[4]");
    private final SelenideElement generatePassButton1 = $x("(//input[@name='passwordGenerator'])[1]");
    private final SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");


    public ClickHouseClusterOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
