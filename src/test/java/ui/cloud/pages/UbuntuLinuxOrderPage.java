package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UbuntuLinuxOrderPage extends NewOrderPage {

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public UbuntuLinuxOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails(){
        super.checkOrderDetails();
        getHardDrive2().shouldBe(Condition.visible);
    }
}
