package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import static core.helper.StringUtils.$x;

@Getter
public class EtcdOrderPage extends NewOrderPage {

    private final Select numberNodes = Select.byLabel("Количество нод");
    private final Input nameUser = Input.byLabel("Пользователь etcd");
    private final Input nameCluster = Input.byLabel("Имя кластера");
    private final Input nameDB = Input.byLabel("Имя базы данных");
    private final SelenideElement generatePassButton = $x("//button[@aria-label='generate']");


    public EtcdOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
