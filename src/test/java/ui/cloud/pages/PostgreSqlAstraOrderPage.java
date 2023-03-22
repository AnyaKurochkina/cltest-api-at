package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Select;

import java.util.UUID;

@Getter
public class PostgreSqlAstraOrderPage extends NewOrderPage {

    Select pSqlVersion = Select.byLabel("Версия PostgreSQL");
    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public PostgreSqlAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
