package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PostgreSqlClusterAstraOrderPage extends NewOrderPage {

    public PostgreSqlClusterAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails(){
        super.checkOrderDetails();
        getHardDrive2().shouldBe(Condition.visible);
    }
}
