package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PodmanOrderPage extends NewOrderPage {

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public PodmanOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
