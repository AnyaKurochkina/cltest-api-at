package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class KafkaServiceOrderPage extends NewOrderPage {

    Input nameCluster = Input.byLabel("Имя кластера *");
    Input nameTopic = Input.byLabel("Имя Topic");
    protected Select sizeTopic = Select.byLabel("Размер топика Apache Kafka");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    private final SelenideElement topicService = $x("//div[contains(text(),'Топик kafka как услуга')]");

    public KafkaServiceOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        getTopicService().shouldBe(Condition.visible);
    }
}
