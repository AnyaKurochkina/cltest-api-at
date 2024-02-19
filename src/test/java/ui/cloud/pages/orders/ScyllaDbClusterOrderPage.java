package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Select;

@Getter
public class ScyllaDbClusterOrderPage extends NewOrderPage {

    private final Select scyllaDbVersionSelect = Select.byLabel("Версия ScyllaDB");

    public ScyllaDbClusterOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
