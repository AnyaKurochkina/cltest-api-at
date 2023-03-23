package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.Input;

@Getter
public class ApacheKafkaClusterOrderPage extends NewOrderPage {

    Input nameCluster = Input.byLabel("Имя кластера *");
    SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");

    public ApacheKafkaClusterOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
