package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.cloud.pages.orders.NewOrderPage;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

import static core.helper.StringUtils.$x;

@Getter
public class WildFlyAstraOrderPage extends NewOrderPage {

    Select groupWildFly = Select.byLabel("Группа управления WildFly");

    public WildFlyAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
