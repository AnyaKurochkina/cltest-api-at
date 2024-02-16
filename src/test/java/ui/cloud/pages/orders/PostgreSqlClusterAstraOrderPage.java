package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;

@Getter
public class PostgreSqlClusterAstraOrderPage extends NewOrderPage {

    public PostgreSqlClusterAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
