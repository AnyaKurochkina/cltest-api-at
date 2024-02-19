package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UbuntuLinuxOrderPage extends NewOrderPage {

    private final String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public UbuntuLinuxOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
