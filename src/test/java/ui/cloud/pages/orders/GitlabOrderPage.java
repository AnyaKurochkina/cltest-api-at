package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.TextArea;

import java.util.UUID;

@Getter
public class GitlabOrderPage extends NewOrderPage {

    private final Input label = Input.byLabel("Метка");
    private final Input projectName = Input.byLabel("Название");
    private final DropDown role = DropDown.byLabel("Роль");
    private final TextArea participant = TextArea.byName("users");
    private final DropDown participant2 = DropDown.byLabel("Участники");

    private final String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public GitlabOrderPage() {
        label.setValue(labelValue);
    }

    public void checkOrderDetails() {
        if (getCalculationDetails().exists()) {
            getCalculationDetails().shouldBe(Condition.visible.because("Должно отображаться сообщение")).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        getOpMemory().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
