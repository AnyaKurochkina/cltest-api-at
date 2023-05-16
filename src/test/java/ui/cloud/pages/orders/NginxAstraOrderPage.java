package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;


@Getter
public class NginxAstraOrderPage extends NewOrderPage {
    public NginxAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
