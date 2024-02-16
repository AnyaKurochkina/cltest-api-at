package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import ui.elements.Select;

import java.util.UUID;

@Getter
public class S3CephTenantOrderPage extends NewOrderPage {

    private final Select redisVersion = Select.byLabel("Версия Redis");
    private final SelenideElement generatePassButton = StringUtils.$x("//button[@aria-label='generate']");
    private final String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public S3CephTenantOrderPage() {
        labelInput.setValue(labelValue);
    }

    public void checkOrderDetails() {
        if (getCalculationDetails().shouldBe(Condition.visible.because("Должно отображаться сообщение")).exists()) {
            getCalculationDetails().shouldBe(Condition.visible.because("Должно отображаться сообщение")).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        getOpMemory().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
