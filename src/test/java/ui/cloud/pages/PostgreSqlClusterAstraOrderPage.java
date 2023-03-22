package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PostgreSqlClusterAstraOrderPage extends NewOrderPage {

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public PostgreSqlClusterAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
