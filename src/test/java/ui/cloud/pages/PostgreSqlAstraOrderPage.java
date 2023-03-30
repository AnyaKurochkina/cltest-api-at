package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Select;

import java.util.UUID;

@Getter
public class PostgreSqlAstraOrderPage extends NewOrderPage {

    Select pSqlVersion = Select.byLabel("Версия PostgreSQL");

    public PostgreSqlAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails(){
        super.checkOrderDetails();
        getHardDrive2().shouldBe(Condition.visible);
    }
}
