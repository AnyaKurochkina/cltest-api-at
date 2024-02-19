package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.Input;

import java.util.UUID;

@Getter
public class GrafanaOrderPage extends NewOrderPage {

    private final String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);
    private final Input createUser = Input.byLabel("Имя пользователя");
    private final Button generatePassButton = Button.byAriaLabel("generate");

    public GrafanaOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
