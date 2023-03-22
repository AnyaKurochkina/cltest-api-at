package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class AstraLinuxOrderPage extends NewOrderPage {

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public AstraLinuxOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
