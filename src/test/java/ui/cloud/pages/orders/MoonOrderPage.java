package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

@Getter
public class MoonOrderPage extends NewOrderPage {

    private final DropDown cluster = DropDown.byLabel("Кластер OpenShift");
    private final Input countSession = Input.byLabel("Количество параллельных сессий");
    private final Input projectName = Input.byLabel("Имя проекта");

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public MoonOrderPage() {
        labelInput.setValue(labelValue);
    }

    public void checkOrderDetails() {
        if (getCalculationDetails().exists()) {
            getCalculationDetails().shouldBe(Condition.visible.because("Должно отображаться сообщение")).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        getOpMemory().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
