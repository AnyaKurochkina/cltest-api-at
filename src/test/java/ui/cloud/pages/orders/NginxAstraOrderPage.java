package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

import static core.helper.StringUtils.$x;

@Getter
public class NginxAstraOrderPage extends NewOrderPage {
    public NginxAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
