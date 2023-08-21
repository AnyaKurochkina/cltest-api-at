package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Select;

@Getter
public class PostgreSqlAstraOrderPage extends NewOrderPage {

    Select pSqlVersion = Select.byLabel("Версия PostgreSQL");

    public PostgreSqlAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible);
    }
}
