package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Select;

@Getter
public class WindowsOrderPage extends NewOrderPage {

    private final Select roleServer = Select.byLabel("Роль сервера. (данное поле влияет на именование)");

    public WindowsOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
