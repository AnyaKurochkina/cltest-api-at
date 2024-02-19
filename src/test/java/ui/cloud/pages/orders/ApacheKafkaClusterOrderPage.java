package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

@Getter
public class ApacheKafkaClusterOrderPage extends NewOrderPage {

    Input nameCluster = Input.byLabel("Имя кластера *");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    private final Select configCluster = Select.byLabel("Конфигурация кластера");
    private final Select osVersionKafka = Select.byLabel("Версия Apache Kafka");

    public ApacheKafkaClusterOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
