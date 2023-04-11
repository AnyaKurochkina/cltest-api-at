package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

@Getter
public class MoonOrderPage extends NewOrderPage {

    DropDown cluster = DropDown.byLabel("Кластер OpenShift");
    Input countSession = Input.byLabel("Количество параллельных сессий");
    Input projectName = Input.byLabel("Имя проекта");

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public MoonOrderPage() {
        labelInput.setValue(labelValue);
    }

    public void checkOrderDetails() {
        if (getCalculationDetails().exists()) {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
