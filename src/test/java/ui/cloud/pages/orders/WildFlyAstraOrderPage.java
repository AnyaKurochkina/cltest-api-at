package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Select;

@Getter
public class WildFlyAstraOrderPage extends NewOrderPage {

    Select groupWildFly = Select.byLabel("Группа управления WildFly");

    public WildFlyAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
